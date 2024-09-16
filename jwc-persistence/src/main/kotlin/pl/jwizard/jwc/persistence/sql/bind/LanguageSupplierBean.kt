/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.spi.LanguageSupplier
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.persistence.sql.ColumnDef
import pl.jwizard.jwc.persistence.sql.JdbcKtTemplateBean
import java.math.BigInteger

/**
 * Implementation of the [LanguageSupplier] interface that retrieves language data from a database. It uses
 * [JdbcKtTemplateBean] to query for supported languages and guild-specific languages.
 *
 * @property jdbcKtTemplateBean The [JdbcKtTemplateBean] bean used for database operations.
 * @property environmentBean Provides environment-specific properties, such as the default language.
 * @author Miłosz Gilga
 */
@Component
class LanguageSupplierBean(
	private val jdbcKtTemplateBean: JdbcKtTemplateBean,
	private val environmentBean: EnvironmentBean,
) : LanguageSupplier {

	/**
	 * Retrieves a map of all supported languages from the database.
	 *
	 * @return A map of language tag (keys) and names (values) retrieved from the `bot_langs` table.
	 */
	override fun getLanguages() = jdbcKtTemplateBean.queryForListMap(
		sql = "SELECT tag, name FROM bot_langs",
		key = ColumnDef("tag", String::class),
		value = ColumnDef("name", String::class)
	)

	/**
	 * Retrieves the language associated with a specific guild from the database.
	 *
	 * @param guildId The ID of the guild for which the language is to be retrieved.
	 * @return The language tag associated with the specified guild.
	 */
	override fun getGuildLanguage(guildId: String): String? {
		val sql = "SELECT tag FROM guilds AS g INNER JOIN bot_langs AS l ON g.lang_id = l.id WHERE discord_id = ?"
		return jdbcKtTemplateBean.queryForObject(sql, String::class, guildId)
	}

	/**
	 * Retrieves the ID of a language based on its tag. If the language tag is not found, it attempts to retrieve the ID
	 * of the default language specified in the environment properties. If the default language is also not found, it
	 * returns the ID of the first language in the list.
	 *
	 * @param tag The language tag for which the ID is to be retrieved.
	 * @return The ID of the language corresponding to the provided tag. If the tag is not found, it returns the ID of
	 *         the default language or the first available language ID in the list.
	 */
	override fun getLanguageId(tag: String): BigInteger {
		val sql = "SELECT id FROM bot_langs WHERE tag = ?"
		val language = jdbcKtTemplateBean.queryForObject(sql, BigInteger::class, tag)
		if (language == null) {
			val configLanguage = environmentBean.getProperty<String>(BotProperty.I18N_DEFAULT_LANGUAGE)
			val defaultLanguage = jdbcKtTemplateBean.queryForObject(sql, BigInteger::class, configLanguage)
				?: return jdbcKtTemplateBean.queryForList("SELECT id FROM bot_langs WHERE", BigInteger::class).first()
			return defaultLanguage
		}
		return language
	}
}
