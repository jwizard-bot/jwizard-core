package pl.jwizard.jwc.core.util.ext

import net.dv8tion.jda.api.entities.Member
import pl.jwizard.jwc.core.util.formatQualifier

val Member.name
	get() = user.name

// format: "memberName <@memberId>"
val Member.qualifier
	get() = formatQualifier(name, idLong)
