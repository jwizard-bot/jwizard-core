package pl.jwizard.jwc.exception.command

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class ModuleIsTurnedOffException(
	commandBaseContext: CommandBaseContext,
	moduleId: String?,
	moduleName: String,
	commandName: String,
) : CommandPipelineException(
	commandBaseContext = commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.MODULE_IS_TURNED_OFF,
	args = mapOf("moduleName" to moduleName),
	logMessage = """
		Attempt to invoke command: "$commandName" from currently turned off module: "$moduleId".
	""",
)
