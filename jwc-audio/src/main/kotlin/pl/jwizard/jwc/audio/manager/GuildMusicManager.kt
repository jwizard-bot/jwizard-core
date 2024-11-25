/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.manager

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import org.apache.commons.validator.routines.UrlValidator
import pl.jwizard.jwc.audio.client.AudioNodePool
import pl.jwizard.jwc.audio.client.DistributedAudioClientBean
import pl.jwizard.jwc.audio.loader.QueueTrackLoader
import pl.jwizard.jwc.audio.loader.RadioStreamLoader
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.jdaDebug
import pl.jwizard.jwl.radio.RadioStation
import pl.jwizard.jwl.util.logger
import java.util.concurrent.TimeUnit

/**
 * Manages audio playback in a guild, including queued tracks and radio streams. Responsible for initializing and
 * managing audio clients, loading tracks, and controlling the player's state.
 *
 * @property commandContext The context of the command being executed.
 * @property future The future response associated with the command for async handling.
 * @property bean A container for various utility beans required for the manager.
 * @property audioClient The client responsible for managing Lavalink nodes and audio connections.
 * @author Miłosz Gilga
 */
class GuildMusicManager(
	val bean: MusicManagersBean,
	private val commandContext: CommandBaseContext,
	private val future: TFutureResponse,
	private val audioClient: DistributedAudioClientBean,
) {

	companion object {
		private val log = logger<GuildMusicManager>()
	}

	val audioController = audioClient.audioController

	val cachedPlayer get() = audioClient.getLink(state.context.guild.idLong)?.cachedPlayer
	val createdOrUpdatedPlayer get() = audioClient.getLink(state.context.guild.idLong)!!.createOrUpdatePlayer()

	/**
	 * A thread that manages the process of leaving the voice channel after inactivity.
	 */
	private val leaveAfterInactivityThread = LeaveAfterInactivityThread(this)

	/**
	 * Manages the state of audio playback, including switching between queued tracks and radio streams.
	 */
	val state = AudioStateManagerProvider(this, commandContext, future)

	/**
	 * Starts the leave waiter that will trigger after a specified period of inactivity.
	 */
	fun startLeavingWaiter() {
		val context = state.context
		val time = bean.environment.getGuildProperty<Long>(GuildProperty.LEAVE_NO_TRACKS_SEC, context.guild.idLong)
		val timeUnit = TimeUnit.SECONDS
		val futureTime = leaveAfterInactivityThread.getFutureTime(time, timeUnit)
		leaveAfterInactivityThread.cancelQueuedTasks() // cancel previous queued future events
		leaveAfterInactivityThread.startOnce(time, timeUnit, Pair(time, context))
		log.jdaDebug(context, "Start leaving channel executor. Execute at: %s.", futureTime)
	}

	/**
	 * Stops the leave waiter and cancels any scheduled tasks for leaving due to inactivity.
	 */
	fun stopLeavingWaiter() {
		val removedQueuedTasks = leaveAfterInactivityThread.cancelQueuedTasks()
		log.jdaDebug(state.context, "Purged: %d leaving waiters.", removedQueuedTasks)
	}

	/**
	 * Loads a track or playlist and begins playing it in the guild.
	 *
	 * @param trackName The name or URL of the track to load.
	 * @param context The context of the command that initiated the playback.
	 */
	fun loadAndPlay(trackName: String, context: CommandBaseContext) {
		val searchPrefix = bean.environment.getProperty<String>(BotProperty.LAVALINK_SEARCH_CONTENT_PREFIX)

		val urlValidator = UrlValidator()
		val parsedTrackName = if (urlValidator.isValid(trackName)) {
			trackName.replace(" ", "")
		} else {
			searchPrefix.format(trackName)
		}
		audioController.loadAndTransferToNode(context.guild, AudioNodePool.QUEUED, context.author, context.selfMember) {
			state.setToQueueTrack(context)
			it.loadItem(parsedTrackName).subscribe(QueueTrackLoader(this))
		}
	}

	/**
	 * Loads a radio stream and begins playing it in the guild.
	 *
	 * @param radioStation Current selected [RadioStation] property.
	 * @param context The context of the command that initiated the stream.
	 */
	fun loadAndStream(radioStation: RadioStation, context: CommandBaseContext) {
		audioController.loadAndTransferToNode(context.guild, AudioNodePool.CONTINUOUS, context.author, context.selfMember) {
			state.setToStream(context, radioStation)
			it.loadItem(radioStation.streamUrl).subscribe(RadioStreamLoader(this, radioStation))
		}
	}

	/**
	 * Creates a new embed message builder for sending formatted messages.
	 *
	 * @return A new instance of MessageEmbedBuilder.
	 */
	fun createEmbedBuilder() = MessageEmbedBuilder(bean.i18n, bean.jdaColorStore, state.context)

	/**
	 * Sends a message to the text channel associated with the command context.
	 *
	 * @param message The message to send as an embed.
	 * @param actionRows Optional action rows to include in the message.
	 */
	fun sendMessage(message: MessageEmbed, vararg actionRows: ActionRow) {
		val context = state.context
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(*actionRows)
			.build()
		context.textChannel.let {
			bean.looselyTransportHandler.sendViaChannelTransport(it, response, context.suppressResponseNotifications)
		}
	}

	/**
	 * Disposes of the current audio scheduler and cleans up resources.
	 */
	fun dispose() {
		state.audioScheduler.stopAndDestroy().subscribe()
		leaveAfterInactivityThread.destroy()
	}
}
