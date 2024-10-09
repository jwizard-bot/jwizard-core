/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink

/**
 * Command to stop the current track and clear the queue.
 *
 * This command stops the currently playing track, if any, and clears the entire music queue. It ensures that the bot
 * is in the same voice channel as the user before execution.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.STOP)
class StopClearQueueCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true

	/**
	 * Executes the command to stop the current track and clear the music queue.
	 *
	 * This method stops the track currently playing (if there is one) and clears the entire queue. It sends an
	 * appropriate response message to the user, indicating whether only the queue was cleared or both the current track
	 * was skipped and the queue was cleared.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val playingTrack = manager.cachedPlayer?.track
		val countOfTracks = manager.audioScheduler.queue.queueSize()

		manager.audioScheduler.stopAndDestroy()

		val (i18nSourceKey, args) = if (playingTrack == null) {
			I18nResponseSource.CLEAR_QUEUE to mapOf("countOfTracks" to countOfTracks)
		} else {
			I18nResponseSource.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE to mapOf("currentTrack" to playingTrack.mdTitleLink)
		}
		val message = createEmbedMessage(context)
			.setDescription(i18nSourceKey, args)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
