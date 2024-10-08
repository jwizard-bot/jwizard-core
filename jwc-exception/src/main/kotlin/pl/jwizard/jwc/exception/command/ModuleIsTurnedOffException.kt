/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when a command is invoked from a module that is turned off.
 *
 * This exception indicates that the command execution failed because the module containing the command is currently
 * disabled, preventing its execution.
 *
 * @param commandBaseContext The context in which the command was invoked.
 * @param moduleId The identifier of the module that is turned off.
 * @param moduleName The name of the module that is turned off.
 * @param commandName The name of the command that was attempted to be executed.
 * @author Miłosz Gilga
 */
class ModuleIsTurnedOffException(
	commandBaseContext: CommandBaseContext,
	moduleId: String,
	moduleName: String,
	commandName: String,
) : CommandPipelineExceptionHandler(
	commandBaseContext = commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.MODULE_IS_TURNED_OFF,
	variables = mapOf("moduleName" to moduleName),
	logMessage = "Attempt to invoke command: \"$commandName\" from currently turned off module: \"$moduleId\".",
)
