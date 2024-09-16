/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.spi

import java.math.BigInteger

/**
 * Interface for providing language-related data.
 *
 * Implementations of this interface should supply available languages and the language associated with specific guilds.
 *
 * @author Miłosz Gilga
 */
interface LanguageSupplier {

	/**
	 * Fetches a map of all supported languages.
	 *
	 * @return A map of languages, which key is language tag ex. *en*, *pl* and value is description.
	 */
	fun getLanguages(): Map<String, String>

	/**
	 * Fetches the language associated with a specific guild.
	 *
	 * @param guildId The ID of the guild for which the language is to be retrieved.
	 * @return The language tag associated with the specified guild.
	 */
	fun getGuildLanguage(guildId: String): String?

	/**
	 * Retrieves the ID of a language based on its tag.
	 *
	 * @param tag The tag of the language whose ID is to be retrieved (ex., *en*).
	 * @return The ID of the language corresponding to the provided tag.
	 */
	fun getLanguageId(tag: String): BigInteger
}
