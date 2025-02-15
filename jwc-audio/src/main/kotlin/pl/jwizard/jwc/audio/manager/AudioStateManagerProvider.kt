package pl.jwizard.jwc.audio.manager

import pl.jwizard.jwc.audio.client.AudioNodeType
import pl.jwizard.jwc.audio.scheduler.AudioScheduleHandler
import pl.jwizard.jwc.audio.scheduler.QueueTrackScheduleHandler
import pl.jwizard.jwc.audio.scheduler.RadioStreamScheduleHandler
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.radio.RadioStation

// defining audio state per every guild music manager
class AudioStateManagerProvider(
	private val guildMusicManager: GuildMusicManager,
	derivedContext: GuildCommandContext,
	derivedFuture: TFutureResponse,
) {
	val audioScheduler get() = audioScheduleHandler
	val queueTrackScheduler get() = audioScheduler as QueueTrackScheduleHandler
	val radioStreamScheduler get() = audioScheduler as RadioStreamScheduleHandler

	// audio content being played
	private var audioType: AudioNodeType? = null

	// audio schedule handler (for different audio types)
	private var audioScheduleHandler: AudioScheduleHandler =
		QueueTrackScheduleHandler(guildMusicManager)

	var context = derivedContext
		private set

	var future = derivedFuture
		private set

	fun setToQueueTrack(context: GuildCommandContext) {
		updateState(AudioNodeType.QUEUED, context)
		if (audioScheduleHandler !is QueueTrackScheduleHandler) {
			// IMPORTANT! init new instance only if previous audio schedule handler is not type QUEUE
			// otherwise queue mechanism will not work (queue will be created at every play new position)
			audioScheduleHandler = QueueTrackScheduleHandler(guildMusicManager)
		}
	}

	fun setToStream(context: GuildCommandContext, radioStation: RadioStation) {
		updateState(AudioNodeType.CONTINUOUS, context)
		// init new instance at every changing audio state
		audioScheduleHandler = RadioStreamScheduleHandler(guildMusicManager, radioStation)
	}

	fun updateStateHandlers(future: TFutureResponse, context: GuildCommandContext) {
		this.future = future
		this.context = context
	}

	fun clearAudioType() {
		audioType = null
	}

	fun isDeclaredAudioContentTypeOrNotYetSet(
		audioType: AudioNodeType,
	) = this.audioType == null || this.audioType == audioType

	private fun updateState(audioType: AudioNodeType, context: GuildCommandContext) {
		this.audioType = audioType
		this.context = context
	}
}
