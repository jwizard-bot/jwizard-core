/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.color

import pl.jwizard.jwc.core.property.BotProperty

/**
 * Enum representing various colors used in the JDA (Java Discord API). Each color is associated with a specific bot
 * property for easy retrieval.
 *
 * @property botProperty The BotProperty associated with the color.
 * @author Miłosz Gilga
 */
enum class JdaColor(val botProperty: BotProperty) {

	/**
	 * Primary color used in the bot's interface.
	 */
	PRIMARY(BotProperty.JDA_COLOR_PRIMARY),

	/**
	 * Secondary color used in the bot's interface.
	 */
	SECONDARY(BotProperty.JDA_COLOR_SECONDARY),

	/**
	 * Tint color, often used for highlighting or accents.
	 */
	TINT(BotProperty.JDA_COLOR_TINT),

	/**
	 * Error color, typically used to indicate danger or errors.
	 */
	ERROR(BotProperty.JDA_COLOR_DANGER),
}
