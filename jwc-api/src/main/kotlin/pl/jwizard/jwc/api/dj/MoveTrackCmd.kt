/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.CommandContext
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

/**
 * Command to move an audio track from one position to another in the queue.
 *
 * This command allows the user to change the position of a track in the music queue by specifying the current position
 * and the target position. If the positions are the same or out of bounds, appropriate exceptions are thrown. Once the
 * track is moved, a confirmation message is sent to the user.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.QUEUE_MOVE)
class MoveTrackCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<MoveTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the command to move a track in the queue.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val fromPos = context.getArg<Int>(Argument.FROM_POS)
		val toPos = context.getArg<Int>(Argument.TO_POS)

		if (fromPos == toPos) {
			throw TrackPositionsIsTheSameException(context)
		}
		val queue = manager.state.queueTrackScheduler.queue
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
