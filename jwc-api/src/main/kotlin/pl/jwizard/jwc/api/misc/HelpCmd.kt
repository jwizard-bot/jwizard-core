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
 * Command class responsible for handling the `Help` command. This command provides a list of available commands in
 * the bot and helps the user navigate through them.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.HELP)
class HelpCmd(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * Executes the Help command, sending a paginated response to the user with the list of available commands.
	 *
	 * The method first retrieves the enabled commands for the guild based on the event type (slash or text command).
	 * It then filters the cached commands to include only those available in the current guild. After that,
	 * the method uses [commandHelpMessageBean] to generate help messages, splits them into pages, and sends them
	 * with paginator buttons.
	 *
	 * @param context The command context, which contains information about the guild, user, and message event.
	 * @param response The future response object, which allows sending the command response asynchronously.
	 */
	override fun execute(context: CommandContext, response: TFutureResponse) {
		val enabledCommands = commandDataSupplier.getEnabledGuildCommandKeys(context.guildDbId, context.isSlashEvent)
		val guildCommands = commandsCacheBean.commands.filter { it.key in enabledCommands }

		val messages = commandHelpMessageBean.createHelpComponents(context, guildCommands)

		val paginator = createPaginator(context, messages)
		val row = paginator.createPaginatorButtonsRow()
		val initMessage = paginator.initPaginator()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(initMessage)
			.addActionRows(row)
			.build()

		response.complete(commandResponse)
	}
}
