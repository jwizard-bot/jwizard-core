/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote

import pl.jwizard.jwl.i18n.I18nLocaleSource

/**
 * A data class that represents a localized message with optional arguments.
 *
 * This class is used to store a message of type [T] (which is an implementation of [I18nLocaleSource]) along with any
 * arguments that need to be formatted within the message.
 *
 * @param T The type of the message, which must extend from [I18nLocaleSource].
 * @property message The localized message to be displayed.
 * @property args A map of arguments that can be injected into the message for dynamic content. Keys are the placeholder
 *           names and values are the corresponding dynamic content.
 * @author Miłosz Gilga
 */
data class I18nMessageWithArgs<T : I18nLocaleSource>(
	val message: T,
	val args: Map<String, Any?>,
)
