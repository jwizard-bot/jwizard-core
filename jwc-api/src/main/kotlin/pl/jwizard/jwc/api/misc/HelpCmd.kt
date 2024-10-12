/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.misc

import pl.jwizard.jwc.api.HelpCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.TFutureResponse

/**
 * Command class responsible for handling the `Help` command. This command provides a list of available commands in
 * the bot and helps the user navigate through them.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.HELP)
class HelpCmd(commandEnvironment: CommandEnvironmentBean) : HelpCommandBase(commandEnvironment) {

	/**
	 * Retrieves and returns a map of commands that are enabled for the current guild. This method is invoked by the
	 * [HelpCommandBase] class to display available bot commands.
	 *
	 * @param context The context of the command execution, containing guild, user, and event information.
	 * @param response The future response object used to send back the command output to Discord.
	 * @return A map of command names and their corresponding details, filtered based on the guild's settings.
	 */
	override fun executeHelp(context: CommandContext, response: TFutureResponse): Map<String, CommandDetails> {
		val enabledCommands = commandDataSupplier.getEnabledGuildCommandKeys(context.guildDbId, context.isSlashEvent)
		return commandsCacheBean.commands.filter { it.key in enabledCommands }
	}
}
