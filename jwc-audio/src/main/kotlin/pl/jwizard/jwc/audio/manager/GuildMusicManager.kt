package pl.jwizard.jwc.audio.manager

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.audio.client.AudioNodeType
import pl.jwizard.jwc.audio.client.DistributedAudioClientImpl
import pl.jwizard.jwc.audio.loader.QueueTrackLoader
import pl.jwizard.jwc.audio.loader.RadioStreamLoader
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.isValidUrl
import pl.jwizard.jwc.core.util.jdaDebug
import pl.jwizard.jwc.exception.audio.AnyNodeInPoolIsNotAvailableException
import pl.jwizard.jwl.radio.RadioStation
import pl.jwizard.jwl.util.logger
import java.util.concurrent.TimeUnit

class GuildMusicManager(
	val bean: MusicManagersCache,
	private val audioClient: DistributedAudioClientImpl,
	commandContext: GuildCommandContext,
	future: TFutureResponse,
) {
	companion object {
		private val log = logger<GuildMusicManager>()
	}

	val cachedPlayer
		get() = link?.cachedPlayer

	val createdOrUpdatedPlayer
		get() = link!!.createOrUpdatePlayer()

	private val link
		get() = audioClient.getLink(state.context.guild.idLong)

	private val leaveAfterInactivityThread = LeaveAfterInactivityThread(this, audioClient)

	val state = AudioStateManagerProvider(this, commandContext, future)

	// init thread for leaving channel after T time of inactivity
	fun startLeavingWaiter() {
		val context = state.context
		val time = bean.guildEnvironment.getGuildProperty<Long>(
			GuildProperty.LEAVE_NO_TRACKS_SEC,
			context.guild.idLong,
		)
		val timeUnit = TimeUnit.SECONDS
		val futureTime = leaveAfterInactivityThread.getFutureTime(time, timeUnit)
		leaveAfterInactivityThread.cancelQueuedTasks() // cancel previous queued future events
		leaveAfterInactivityThread.startOnce(time, timeUnit, Pair(time, context))
		log.jdaDebug(context, "Start leaving channel executor. Execute at: %s.", futureTime)
	}

	fun stopLeavingWaiter() {
		val removedQueuedTasks = leaveAfterInactivityThread.cancelQueuedTasks()
		log.jdaDebug(state.context, "Purged: %d leaving waiters.", removedQueuedTasks)
	}

	// load QUEUED audio content
	fun loadAndPlay(trackName: String, context: GuildCommandContext) {
		val searchPrefix = bean.environment
			.getProperty<String>(BotProperty.AUDIO_SERVER_SEARCH_DEFAULT_CONTENT_PREFIX)
		val parsedTrackName = if (isValidUrl(trackName)) {
			trackName.replace(" ", "")
		} else {
			searchPrefix.format(trackName)
		}
		val nodePool = AudioNodeType.QUEUED
		val anyNodeInPoolExist = audioClient.loadAndTransferToNode(context, nodePool) {
			state.setToQueueTrack(context)
			it.loadItem(parsedTrackName).subscribe(QueueTrackLoader(this))
		}
		if (!anyNodeInPoolExist) {
			throw AnyNodeInPoolIsNotAvailableException(context, nodePool.poolName)
		}
	}

	// load CONTINUOUS audio content (radio station)
	fun loadAndStream(radioStation: RadioStation, context: GuildCommandContext) {
		val nodePool = AudioNodeType.CONTINUOUS
		val anyNodeInPoolExist = audioClient.loadAndTransferToNode(context, nodePool) {
			state.setToStream(context, radioStation)
			it.loadItem(radioStation.streamUrl).subscribe(RadioStreamLoader(this, radioStation))
		}
		if (!anyNodeInPoolExist) {
			throw AnyNodeInPoolIsNotAvailableException(context, nodePool.poolName)
		}
	}

	fun createEmbedBuilder() = MessageEmbedBuilder(bean.i18n, bean.jdaColorStore, state.context)

	fun sendMessage(message: MessageEmbed, vararg actionRows: ActionRow) {
		val context = state.context
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(*actionRows)
			.build()
		context.textChannel.let {
			bean.looselyTransportHandler.sendViaChannelTransport(
				it,
				response,
				context.suppressResponseNotifications
			)
		}
	}

	fun dispose() = leaveAfterInactivityThread.destroy()
}
