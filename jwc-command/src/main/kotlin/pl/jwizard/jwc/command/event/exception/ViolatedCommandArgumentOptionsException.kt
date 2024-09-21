/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.exception

import pl.jwizard.jwc.core.exception.CommandPipelineException
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext

/**
 * Exception thrown when a command is invoked with an argument that violates the accepted options for that argument.
 *
 * This exception is triggered when a user provides a value for a command argument that is not among the accepted
 * options, indicating that the invocation of the command failed due to invalid argument input.
 *
 * @param commandBaseContext The context in which the command was invoked.
 * @param violatedArgName The name of the argument that has been violated.
 * @param violatedValue The value provided for the violated argument.
 * @param acceptedValueList A list of accepted values for the argument.
 * @param acceptedValuesFormatted A formatted string representation of the accepted values.
 * @author Miłosz Gilga
 */
class ViolatedCommandArgumentOptionsException(
	commandBaseContext: CommandBaseContext,
	violatedArgName: String,
	violatedValue: Any,
	acceptedValueList: List<String>,
	acceptedValuesFormatted: String,
) : CommandPipelineException(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.VIOLATED_COMMAND_ARG_OPTIONS,
	variables = mapOf(
		"violatedArgName" to violatedArgName,
		"acceptedValueList" to acceptedValuesFormatted,
	),
	logMessage = """
		Attempt to invoke command with violated argument: "$violatedArgName" options.
		Violated value: "$violatedValue". Accepted values: "$acceptedValueList".
	"""
)
