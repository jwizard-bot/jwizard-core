/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.formatQualifier

/**
 * Extension property that formats the guild information into a user-friendly qualifier.
 *
 * @receiver CommandBaseContext The context of the command execution.
 * @return A formatted string representing the guild.
 * @author Miłosz Gilga
 * @see formatQualifier
 */
val CommandBaseContext.guildQualifier get() = formatQualifier(guildName, guildId)

/**
 * Extension property that formats the author information into a user-friendly qualifier.
 *
 * @receiver CommandBaseContext The context of the command execution.
 * @return A formatted string representing the author.
 * @author Miłosz Gilga
 * @see formatQualifier
 */
val CommandBaseContext.authorQualifier get() = formatQualifier(authorName, authorId)
