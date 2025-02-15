package pl.jwizard.jwc.core.util.ext

import net.dv8tion.jda.api.entities.channel.Channel
import pl.jwizard.jwc.core.util.formatQualifier

// format: "channelName <@channelId>"
val Channel.qualifier
	get() = formatQualifier(name, idLong)
