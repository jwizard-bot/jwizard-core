/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.spi.LanguageSupplier
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Implementation of the [LanguageSupplier] interface that retrieves language data from a database. It uses
 * [JdbcTemplateBean] to query for supported languages and guild-specific languages.
 *
 * @property jdbcTemplateBean The [JdbcTemplateBean] bean used for database operations.
 * @property environmentBean Provides environment-specific properties, such as the default language.
 * @author Miłosz Gilga
 */
@Component
class LanguageSupplierBean(
	private val jdbcTemplateBean: JdbcTemplateBean,
	private val environmentBean: EnvironmentBean,
) : LanguageSupplier {

	companion object {
		private val log = LoggerFactory.getLogger(LanguageSupplierBean::class.java)
	}

	/**
	 * Retrieves a list of all supported languages from the database.
	 *
	 * @return A list of language tags retrieved from the `bot_langs` table.
	 */
	override fun fetchLanguages(): List<String> =
		jdbcTemplateBean.queryForList("SELECT tag FROM bot_langs", String::class.java)

	/**
	 * Retrieves the language associated with a specific guild from the database. If no language is found for the guild,
	 * the default language is returned (defined in YAML config file).
	 *
	 * @param guildId The ID of the guild for which the language is to be retrieved.
	 * @return The language tag associated with the specified guild, or the default language if not found.
	 * @see BotProperty.I18N_DEFAULT_LANGUAGE
	 */
	override fun fetchGuildLanguage(guildId: String): String {
		val sql = "SELECT tag FROM guilds AS g INNER JOIN bot_langs AS l ON g.lang_id = l.id WHERE discord_id = ?"
		val result = jdbcTemplateBean.queryForObject(sql, String::class.java, guildId)
		if (result.isEmpty()) {
			val defValue = environmentBean.getProperty<String>(BotProperty.I18N_DEFAULT_LANGUAGE)
			log.debug("Persisted guild language not found. Return default value: {}.", defValue)
			return defValue
		}
		return result
	}
}
