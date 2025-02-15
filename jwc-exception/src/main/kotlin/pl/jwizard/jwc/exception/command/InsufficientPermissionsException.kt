package pl.jwizard.jwc.exception.command

import net.dv8tion.jda.api.Permission
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class InsufficientPermissionsException(
	context: CommandBaseContext,
	formattedPermission: String,
	requiredPermission: Permission,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.INSUFFICIENT_PERMISSIONS_EXCEPTION,
	args = mapOf("permissions" to formattedPermission),
	logMessage = """
		Attempt to invoke command: "${context.commandName}" without requested permission:
		"$requiredPermission".
	""",
)
