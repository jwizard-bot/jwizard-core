/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.guild

import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.property.guild.GuildPropertyConverter.*
import pl.jwizard.jwc.core.util.secToDTF

/**
 * Enum that defines different types of conversion for guild properties. Each converter provides a mapping function
 * that transforms the input value. Some converters may also handle internationalized (i18n) content.
 *
 * Defining following properties:
 *
 * - [BASE]: Basic converter that returns the value as it is without any modification.
 * - [TO_DTF_SEC]: Converts the input value from seconds to a DateTime format (DTF).
 * - [TO_PERCENTAGE]: Converts the input value to a percentage format.
 * - [TO_BOOL]: Converts a Boolean value to its internationalized representation.
 *
 * @property mapper The function that converts the given property value to the desired format.
 * @property isI18nContent Indicates whether the converter processes internationalized content.
 * @author Miłosz Gilga
 */
enum class GuildPropertyConverter(
	val mapper: (Any) -> Any,
	val isI18nContent: Boolean = false,
) {

	/**
	 * Basic converter that returns the value as it is without any modification.
	 */
	BASE({ it }),

	/**
	 * Converts the input value from seconds to a DateTime format (DTF). Assumes the input value is a Long representing
	 * seconds.
	 */
	TO_DTF_SEC({ secToDTF(it as Long) }),

	/**
	 * Converts the input value to a percentage format by appending a percentage symbol ("%").
	 */
	TO_PERCENTAGE({ "${it}%" }),

	/**
	 * Converts a Boolean value to its internationalized representation. Returns an i18n key for "TURN ON" if true,
	 * or "TURN OFF" if false.
	 */
	TO_BOOL({ if (it == true) I18nUtilSource.TURN_ON else I18nUtilSource.TURN_OFF }, true)
}
