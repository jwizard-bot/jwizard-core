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
