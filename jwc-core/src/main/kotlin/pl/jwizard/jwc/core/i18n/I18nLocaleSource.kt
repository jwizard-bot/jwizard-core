/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n

/**
 * Interface for providing internationalization (i18n) placeholders.
 *
 * This interface defines a contract for classes that need to provide a placeholder string
 * which will be used to retrieve localized messages from a message source. Implementing
 * classes should provide a placeholder that maps to a specific message key in the localization
 * resources.
 *
 * Implementing classes can use the [getPlaceholder] method to return the key used to fetch
 * localized strings from the message source.
 *
 * @author Miłosz Gilga
 */
interface I18nLocaleSource {

	/**
	 * Returns the placeholder key used to retrieve the localized message.
	 *
	 * The placeholder is a string that maps to a key in the localization resource files.
	 * Implementing classes should return the specific placeholder string associated with
	 * their messages.
	 *
	 * @return The placeholder key for retrieving the localized message.
	 */
	fun getPlaceholder(): String
}
