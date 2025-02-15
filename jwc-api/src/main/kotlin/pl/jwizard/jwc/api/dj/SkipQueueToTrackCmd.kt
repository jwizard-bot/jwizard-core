package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackOffsetOutOfBoundsException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.SKIPTO)
internal class SkipQueueToTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : DjCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<SkipQueueToTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true
	override val queueShouldNotBeEmpty = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val position = context.getArg<Int>(Argument.POS)

		val queue = manager.state.queueTrackScheduler.queue
		// check, if position is not exceed queue size
		if (queue.positionIsOutOfBounds(position)) {
			throw TrackOffsetOutOfBoundsException(context, position, queue.size)
		}
		val currentTrack = queue.skipToPosition(position)!!

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setTrack(currentTrack),
			onSuccess = {
				log.jdaInfo(
					context,
					"Skipped: %d tracks in queue and started playing: %s.",
					position,
					currentTrack.qualifier
				)
				createEmbedMessage(context)
					.setDescription(
						i18nLocaleSource = I18nResponseSource.SKIP_TO_SELECT_TRACK_POSITION,
						args = mapOf(
							"countOfSkippedTracks" to position - 1,
							"currentTrack" to currentTrack.mdTitleLink,
						)
					)
					.setColor(JdaColor.PRIMARY)
					.build()
			},
		)
	}
}
