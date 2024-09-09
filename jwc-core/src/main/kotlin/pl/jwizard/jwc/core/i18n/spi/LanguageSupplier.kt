/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.spi

/**
 * Interface for providing language-related data.
 *
 * Implementations of this interface should supply available languages and the language associated with specific guilds.
 *
 * @author Miłosz Gilga
 */
interface LanguageSupplier {

	/**
	 * Fetches a list of all supported languages.
	 *
	 * @return A list of language tags, such as "en", "pl", etc.
	 */
	fun fetchLanguages(): List<String>

	/**
	 * Fetches the language associated with a specific guild.
	 *
	 * @param guildId The ID of the guild for which the language is to be retrieved.
	 * @return The language tag associated with the specified guild.
	 */
	fun fetchGuildLanguage(guildId: String): String
}
