package pl.jwizard.jwc.exception.user

import net.dv8tion.jda.api.entities.channel.Channel
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

class UserOnVoiceChannelWithBotNotFoundException(
	context: CommandBaseContext,
	userChannel: Channel?,
	botChannel: Channel?,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND,
	logMessage = """
		Attempt to invoke command while user is not in voice channel with bot.
		User channel: ${userChannel?.qualifier}. Bot channel: ${botChannel?.qualifier}.
  """,
)
