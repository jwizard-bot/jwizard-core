package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class ViolatedCommandArgumentOptionsException(
	context: CommandBaseContext,
	violatedArgName: String,
	violatedValue: Any?,
	acceptedValueList: List<String>,
	acceptedValuesFormatted: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.VIOLATED_COMMAND_ARG_OPTIONS,
	args = mapOf(
		"violatedArgName" to violatedArgName,
		"acceptedValueList" to acceptedValuesFormatted,
	),
	logMessage = """
		Attempt to invoke command with violated argument: "$violatedArgName" options. Violated value:
		"$violatedValue". Accepted values: "$acceptedValueList".
	""",
)
