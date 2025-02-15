package pl.jwizard.jwc.core.util.ext

import net.dv8tion.jda.api.entities.Guild
import pl.jwizard.jwc.core.util.formatQualifier

// format: "guildName <@guildId>"
val Guild.qualifier
	get() = formatQualifier(name, idLong)
