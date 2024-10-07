/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.misc

import pl.jwizard.jwc.api.HelpCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.TFutureResponse

/**
 * Command class responsible for handling the `HelpMe` command. This command provides a personalized help message to
 * the user who invoked it.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.HELPME)
class HelpMeCmd(commandEnvironment: CommandEnvironmentBean) : HelpCommandBase(commandEnvironment) {

	/**
	 * Returns a map of all available commands, as this command provides a comprehensive help message. This method is
	 * invoked by [HelpCommandBase] to display the complete list of commands to the user.
	 *
	 * @param context The context of the command execution, containing guild, user, and event information.
	 * @param response The future response object used to send back the command output to Discord.
	 * @return A map of all available commands and their corresponding details (CommandDetails).
	 */
	override fun executeHelp(context: CommandContext, response: TFutureResponse) = commandsCacheBean.commands

	/**
	 * Determines, that this help command be executed in a private context.
	 *
	 * @param context The command context, which contains information about the guild, user, and message event.
	 * @return The author's ID if the command is invoked in a private context, otherwise null.
	 */
	override fun isPrivate(context: CommandContext) = context.authorId
}
