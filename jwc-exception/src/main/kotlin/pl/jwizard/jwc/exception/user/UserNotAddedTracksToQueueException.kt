package pl.jwizard.jwc.exception.user

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class UserNotAddedTracksToQueueException(
	context: CommandBaseContext,
	userId: Long,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.USER_NOT_ADDED_TRACKS_TO_QUEUE,
	logMessage = """
		Attempt to perform action on tracks from user: "$userId" which not added any track in queue.
	""",
)
