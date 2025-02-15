package pl.jwizard.jwc.core.jda.color

import pl.jwizard.jwc.core.property.BotProperty

enum class JdaColor(val botProperty: BotProperty) {
	PRIMARY(BotProperty.JDA_COLOR_PRIMARY),
	ERROR(BotProperty.JDA_COLOR_DANGER),
}
