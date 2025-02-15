package pl.jwizard.jwc.api

import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.command.UnauthorizedManagerException

internal abstract class ManagerCommandBase(
	commandEnvironment: CommandEnvironmentBean,
) : CommandBase(commandEnvironment) {
	final override fun execute(context: GuildCommandContext, response: TFutureResponse) {
		if (!context.checkIfAuthorHasPermissions(*(superuserPermissions.toTypedArray()))) {
			throw UnauthorizedManagerException(context)
		}
		executeManager(context, response)
	}

	protected abstract fun executeManager(context: GuildCommandContext, response: TFutureResponse)
}
