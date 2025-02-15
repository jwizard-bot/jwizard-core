package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class AnyNodeInPoolIsNotAvailableException(
	context: CommandBaseContext,
	nodePoolName: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.ANY_NODE_IN_POOL_IS_NOT_AVAILABLE,
	logMessage = "Attempt to invoke audio command when selected node pool: \"$nodePoolName\" is empty.",
)
