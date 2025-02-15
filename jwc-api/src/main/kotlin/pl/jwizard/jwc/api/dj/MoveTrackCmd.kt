package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackOffsetOutOfBoundsException
import pl.jwizard.jwc.exception.track.TrackPositionsIsTheSameException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger
import kotlin.math.abs

@JdaCommand(Command.QUEUE_MOVE)
class MoveTrackCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<MoveTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val fromPos = context.getArg<Int>(Argument.FROM_POS)
		val toPos = context.getArg<Int>(Argument.TO_POS)

		if (fromPos == toPos) {
			throw TrackPositionsIsTheSameException(context)
		}
		val queue = manager.state.queueTrackScheduler.queue
		// check, if fromPos or toPos exceed queue size
		if (queue.positionIsOutOfBounds(fromPos) || queue.positionIsOutOfBounds(toPos)) {
			throw TrackOffsetOutOfBoundsException(context, abs(fromPos - toPos), queue.size)
		}
		val movedTrack = queue.moveToPosition(fromPos, toPos)
		log.jdaInfo(
			context,
			"Audio track: %s was moved from: %d to: %s position in queue.",
			movedTrack.qualifier,
			fromPos,
			toPos
		)
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.MOVE_TRACK_POS_TO_SELECTED_LOCATION,
				args = mapOf(
					"movedTrack" to movedTrack.mdTitleLink,
					"previousPosition" to fromPos,
					"requestedPosition" to toPos,
				)
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
