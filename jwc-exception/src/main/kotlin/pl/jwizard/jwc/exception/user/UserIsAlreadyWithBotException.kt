/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.user

import net.dv8tion.jda.api.entities.channel.Channel
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a user attempts to invoke a command while already being in the same voice channel as the bot.
 *
 * @param context The context of the command that caused the exception.
 * @param channel The voice channel where the user and bot are currently connected.
 * @author Miłosz Gilga
 */
class UserIsAlreadyWithBotException(
	context: CommandBaseContext,
	channel: Channel,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.USER_ID_ALREADY_WITH_BOT,
	logMessage = "Attempt to invoke command, while user is together with bot on channel: ${channel.qualifier}.",
)
