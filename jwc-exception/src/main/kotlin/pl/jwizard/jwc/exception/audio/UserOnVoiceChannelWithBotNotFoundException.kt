/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.audio

import net.dv8tion.jda.api.entities.channel.Channel
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when a command is invoked but the user is not in the same voice channel as the bot.
 *
 * @param commandBaseContext The context of the command that caused the exception.
 * @param userChannel The voice channel where the user is currently connected.
 * @param botChannel The voice channel where the bot is currently connected.
 * @author Miłosz Gilga
 */
class UserOnVoiceChannelWithBotNotFoundException(
	commandBaseContext: CommandBaseContext,
	userChannel: Channel?,
	botChannel: Channel?,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.USER_ON_VOICE_CHANNEL_WITH_BOT_NOT_FOUND,
	logMessage = """
		Attempt to invoke command while user is not in voice channel with bot.
		User channel: "${userChannel?.qualifier}". Bot channel: "${botChannel?.qualifier}".
  """,
)
