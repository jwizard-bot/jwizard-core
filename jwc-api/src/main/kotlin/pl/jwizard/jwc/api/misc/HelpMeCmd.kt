/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.misc

import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse

/**
 * Command class responsible for handling the `HelpMe` command. This command provides a personalized help message to
 * the user who invoked it.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.HELPME)
class HelpMeCmd(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * Executes the HelpMe command, sending a personalized help message to the user.
	 *
	 * This method retrieves help components for all available commands, creates a paginator to handle message pagination,
	 * and sends the initial message as a private message to the user.
	 *
	 * @param context The command context, which contains information about the guild, user, and message event.
	 * @param response The future response object, which allows sending the command response asynchronously.
	 */
	override fun execute(context: CommandContext, response: TFutureResponse) {
		val messages = commandHelpMessageBean.createHelpComponents(context, commandsCacheBean.commands)

		val paginator = createPaginator(context, messages)
		val row = paginator.createPaginatorButtonsRow()
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(row)
			.build()

		response.complete(commandResponse)
	}

	/**
	 * Determines if the command should be executed in a private context.
	 *
	 * @param context The command context, which contains information about the guild, user, and message event.
	 * @return The author's ID if the command is invoked in a private context, otherwise null.
	 */
	override fun isPrivate(context: CommandContext) = context.authorId
}
