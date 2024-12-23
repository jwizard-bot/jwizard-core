/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
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
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to shuffle the current music queue.
 *
 * This command randomizes the order of the tracks in the music queue. It ensures that the bot is in the same voice
 * channel as the user and that the queue is not empty before shuffling. After shuffling, a confirmation message is
 * sent to the user.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.QUEUE_SHUFFLE)
class ShuffleQueueCmd(commandEnvironment: CommandEnvironmentBean) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<ShuffleQueueCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	/**
	 * Executes the command to shuffle the music queue.
	 *
	 * This method shuffles the current queue of tracks and sends a message to confirm the action. If any of the
	 * preconditions fail (such as the user not being in the same channel as the bot, or the queue being empty), the
	 * command will not proceed.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: GuildCommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val queue = manager.state.queueTrackScheduler.queue

		queue.shuffle()
		log.jdaInfo(context, "Current queue of: %d tracks was shuffled.", queue.size)

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.QUEUE_WAS_SHUFFLED,
				args = mapOf("showQueueCmd" to Command.QUEUE_SHOW.parseWithPrefix(context))
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
