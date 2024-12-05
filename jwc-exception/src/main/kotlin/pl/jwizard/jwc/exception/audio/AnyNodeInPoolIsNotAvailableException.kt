/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.audio

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when no available nodes exist in the specified node pool during an audio command execution.
 *
 * @param context The context of the command that triggered this exception, providing details of the execution.
 * @param nodePoolName The name of the node pool that was attempted to be used but found empty.
 * @author Miłosz Gilga
 */
class AnyNodeInPoolIsNotAvailableException(
	context: CommandBaseContext,
	nodePoolName: String,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.ANY_NODE_IN_POOL_IS_NOT_AVAILABLE,
	logMessage = "Attempt to invoke audio command when selected node pool: \"$nodePoolName\" is empty.",
)
