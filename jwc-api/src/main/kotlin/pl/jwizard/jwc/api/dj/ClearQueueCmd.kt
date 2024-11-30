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
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to clear the current music queue in the bot's music manager.
 *
 * This command removes all tracks currently in the queue and notifies the user about the number of removed tracks. It
 * ensures that the bot and the user are in the same voice channel and that the queue is not empty before execution.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.QUEUE_CLEAR)
class ClearQueueCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<ClearQueueCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the command to clear the music queue.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val queueTrackScheduler = manager.state.queueTrackScheduler

		val queueSize = queueTrackScheduler.queue.clearAndGetSize()
		log.jdaInfo(context, "Queue was cleared. Removed: %d audio tracks from queue.", queueSize)

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.CLEAR_QUEUE,
				args = mapOf("countOfTracks" to queueSize),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
