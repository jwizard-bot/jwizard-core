/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import net.dv8tion.jda.api.Permission
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a command execution fails due to insufficient permissions.
 *
 * @param context The context of the command that caused the exception.
 * @param formattedPermission A human-readable string representation of the required permissions.
 * @param requiredPermission  The specific [Permission] that the user lacks to execute the command.
 * @author Miłosz Gilga
 */
class InsufficientPermissionsException(
	context: CommandBaseContext,
	formattedPermission: String,
	requiredPermission: Permission
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.INSUFFICIENT_PERMISSIONS_EXCEPTION,
	args = mapOf("permissions" to formattedPermission),
	logMessage = """
		Attempt to invoke command: "${context.commandName}" without requested permission: "$requiredPermission".
	""",
)
