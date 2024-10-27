/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a command that is intended to be used only for discrete audio tracks is invoked while the
 * current audio source is continuous.
 *
 * @param context The context of the command that caused the exception.
 * @author Miłosz Gilga
 */
class CommandAvailableOnlyForDiscreteTrackException(
	context: CommandBaseContext,
) : CommandPipelineExceptionHandler(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.COMMAND_AVAILABLE_ONLY_FOR_DISCRETE_TRACK,
	logMessage = """
		Attempt to invoke command: \"${context.commandName}\" on current continuous audio source, while is available only
		discrete source.
	""",
)
