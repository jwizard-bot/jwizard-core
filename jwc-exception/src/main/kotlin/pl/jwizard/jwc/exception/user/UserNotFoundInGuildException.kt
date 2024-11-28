/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.user

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when an attempt is made to find a user in the current guild, but the user is not a member.
 *
 * @param context the context of the command where the exception occurred.
 * @param userId the ID of the user who was attempted to be found in the guild but is not present.
 * @author Miłosz Gilga
 */
class UserNotFoundInGuildException(
	context: CommandBaseContext,
	userId: Long,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.USER_NOT_FOUND_IN_GUILD,
	logMessage = "Attempt to find user with ID: \"$userId\" which is not member of current guild.",
)
