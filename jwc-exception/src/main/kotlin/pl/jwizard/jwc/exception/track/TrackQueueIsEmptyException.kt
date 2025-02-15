package pl.jwizard.jwc.exception.track

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class TrackQueueIsEmptyException(context: CommandBaseContext) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.TRACK_QUEUE_IS_EMPTY,
	logMessage = "Attempt to perform action on empty track queue.",
)
