package pl.jwizard.jwc.exception.command

import net.dv8tion.jda.api.entities.channel.Channel
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class ForbiddenChannelException(
	context: CommandBaseContext,
	forbiddenChannel: Channel?,
	acceptedChannel: Channel?,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.FORBIDDEN_CHANNEL,
	args = mapOf("acceptChannel" to acceptedChannel?.name),
	logMessage = """
		Attempt to use song request command on forbidden channel: ${forbiddenChannel?.qualifier}.
		Accepted channel: ${acceptedChannel?.qualifier}.
	""",
)
