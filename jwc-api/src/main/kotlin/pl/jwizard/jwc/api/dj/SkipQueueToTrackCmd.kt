/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.spi.lava.MusicManager
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.logger
import pl.jwizard.jwc.exception.track.TrackOffsetOutOfBoundsException

/**
 * Command to skip to a specific track in the queue.
 *
 * This command allows skipping multiple tracks and starting playback from a specific position in the queue. It ensures
 * that the bot is in the same voice channel as the user, the bot is in playing mode, and that the queue is not empty
 * before executing the command.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.SKIPTO)
class SkipQueueToTrackCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<SkipQueueToTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true
	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the command to skip to a specific track in the queue.
	 *
	 * This method skips a certain number of tracks based on the position provided by the user and starts playing the
	 * selected track. If the position is out of bounds, an exception is thrown.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val position = context.getArg<Int>(CommandArgument.POS)

		val queue = manager.audioScheduler.queue
		if (queue.positionIsOutOfBounds(position)) {
			throw TrackOffsetOutOfBoundsException(context, position, queue.queueSize())
		}
		val currentTrack = queue.skipToPosition(position)
		log.jdaInfo(context, "Skipped: %d tracks in queue and started playing: %s.", position, currentTrack?.qualifier)

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SKIP_TO_SELECT_TRACK_POSITION,
				args = mapOf(
					"countOfSkippedTracks" to position - 1,
					"currentTrack" to currentTrack?.mdTitleLink,
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
