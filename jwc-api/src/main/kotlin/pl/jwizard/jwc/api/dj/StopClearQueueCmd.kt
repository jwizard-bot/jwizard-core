package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackQueueIsEmptyException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.STOP)
class StopClearQueueCmd(
	commandEnvironment: CommandEnvironmentBean
) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<StopClearQueueCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val currentTrack = manager.cachedPlayer?.track
		val queueTrackScheduler = manager.state.queueTrackScheduler

		// check if currently audio player not play any content and queue track is empty
		// if queue is empty, but currentTrack is not null, remove current track otherwise remove all
		// queue and current track
		if (currentTrack == null && queueTrackScheduler.queue.isEmpty()) {
			throw TrackQueueIsEmptyException(context)
		}
		val queueSize = queueTrackScheduler.queue.size
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = queueTrackScheduler.stopAndDestroy(),
			onSuccess = {
				log.jdaInfo(
					context,
					"Stop current track: %s and clear queue. Removed: %d results.",
					currentTrack?.qualifier,
					queueSize,
				)
				val (i18nSourceKey, args) = if (currentTrack == null) {
					// only, if currently not playing any track
					Pair(I18nResponseSource.CLEAR_QUEUE, mapOf("countOfTracks" to queueSize))
				} else {
					Pair(
						I18nResponseSource.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE,
						mapOf("currentTrack" to currentTrack.mdTitleLink)
					)
				}
				createEmbedMessage(context)
					.setDescription(i18nSourceKey, args)
					.setColor(JdaColor.PRIMARY)
					.build()
			},
		)
	}
}
