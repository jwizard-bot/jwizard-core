/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import dev.arbjerg.lavalink.client.player.Track
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.async.AsyncUpdatableHook
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to stop the current track and clear the queue.
 *
 * This command stops the currently playing track, if any, and clears the entire music queue. It ensures that the bot
 * is in the same voice channel as the user before execution.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.STOP)
class StopClearQueueCmd(
	commandEnvironment: CommandEnvironmentBean,
) : DjCommandBase(commandEnvironment), AsyncUpdatableHook<Pair<Track?, Int>> {

	companion object {
		private val log = logger<StopClearQueueCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	/**
	 * Stops the current track and clears the music queue.
	 *
	 * This method stops the currently playing track, if any, and clears the music queue managed by the bot. It checks
	 * if there is a track playing and if the queue has any remaining tracks, then asynchronously stops and clears them.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val playingTrack = manager.cachedPlayer?.track
		val queueTrackScheduler = manager.state.queueTrackScheduler

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = queueTrackScheduler.stopAndDestroy(),
			payload = Pair(playingTrack, queueTrackScheduler.queue.size),
		)
	}

	/**
	 * Handles the asynchronous success response after stopping the track and clearing the queue.
	 *
	 * This method processes the result of the async operation, logs the stop event, and returns an embedded message
	 * to the user indicating whether a track was skipped and how many tracks were cleared from the queue.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param payload A pair containing the previous volume level and the guild music manager.
	 * @return A MessageEmbed containing a confirmation of the volume change and the new volume level.
	 */
	override fun onAsyncSuccess(context: CommandContext, payload: Pair<Track?, Int>): MessageEmbed {
		val (playingTrack, queueSize) = payload
		log.jdaInfo(
			context,
			"Stop current track: %s and clear queue. Removed: %d results.",
			playingTrack?.qualifier,
			queueSize,
		)
		val (i18nSourceKey, args) = if (playingTrack == null) {
			Pair(I18nResponseSource.CLEAR_QUEUE, mapOf("countOfTracks" to queueSize))
		} else {
			Pair(I18nResponseSource.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE, mapOf("currentTrack" to playingTrack.mdTitleLink))
		}
		return createEmbedMessage(context)
			.setDescription(i18nSourceKey, args)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
