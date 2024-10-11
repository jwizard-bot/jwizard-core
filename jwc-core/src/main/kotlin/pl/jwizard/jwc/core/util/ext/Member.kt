/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

import net.dv8tion.jda.api.entities.Member
import pl.jwizard.jwc.core.util.formatQualifier

/**
 * Extension property to retrieve the name of the Discord user associated with the member.
 *
 * @return The name of the user.
 * @author Miłosz Gilga
 */
val Member.name get() = user.name

/**
 * Extension property to retrieve a unique qualifier for the member, typically formatted as a combination of their
 * username and ID.
 *
 * @return A string representing the formatted qualifier of the member.
 * @author Miłosz Gilga
 */
val Member.qualifier get() = formatQualifier(name, idLong)

/**
 * Extension property to retrieve the member's avatar URL if available, or the default avatar URL if the user does not
 * have a custom avatar.
 *
 * @return A string representing the URL of the avatar or the default avatar.
 * @author Miłosz Gilga
 */
val Member.avatarOrDefaultUrl get() = avatarUrl ?: defaultAvatarUrl
