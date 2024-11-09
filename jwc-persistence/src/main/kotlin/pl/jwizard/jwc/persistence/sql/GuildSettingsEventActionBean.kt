/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import org.springframework.transaction.support.TransactionTemplate
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbcKtTemplateBean
import pl.jwizard.jwl.persistence.sql.SqlColumn
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.property.AppBaseProperty
import java.sql.JDBCType

/**
 * Component responsible for managing guild settings, including creation, deletion, and retrieval.
 *
 * @property jdbcKtTemplateBean Bean for executing SQL queries.
 * @property translationTemplate Template for handling transactions.
 * @property environmentBean Bean for fetching environment properties.
 * @author Miłosz Gilga
 */
@SingletonComponent
class GuildSettingsEventActionBean(
	private val jdbcKtTemplateBean: JdbcKtTemplateBean,
	private val translationTemplate: TransactionTemplate,
	private val environmentBean: EnvironmentBean,
) : GuildSettingsEventAction {

	/**
	 * Creates settings for a specific guild in the system. If the guild already has settings, no action is taken and
	 * false is returned.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @param guildLocale Locale used by the guild for language settings.
	 * @return A pair where the first value is true if the settings were created successfully, and the second value
	 *         contains an error message if the creation failed.
	 */
	override fun createGuildSettings(guildId: Long, guildLocale: String): Pair<Boolean, String?> {
		val guildSettingsAlreadyExist = jdbcKtTemplateBean.queryForBool(
			sql = "SELECT COUNT(*) > 0 FROM guilds WHERE discord_id = ?",
			guildId,
		)
		if (guildSettingsAlreadyExist) {
			return Pair(false, null)
		}
		val availableLanguages = environmentBean.getListProperty<String>(AppBaseListProperty.I18N_LANGUAGES)
		val guildLanguage = guildLocale.substring(0, 2)
		val language = if (guildLanguage in availableLanguages) {
			guildLanguage
		} else {
			environmentBean.getProperty<String>(AppBaseProperty.I18N_DEFAULT_LANGUAGE)
		}
		return translationTemplate.execute {
			try {
				val columns = mapOf(
					"discord_id" to SqlColumn(guildId, JDBCType.BIGINT),
					"language" to SqlColumn(language, JDBCType.VARCHAR),
				)
				jdbcKtTemplateBean.insertMultiples("guilds", columns)
				Pair(true, null)
			} catch (ex: Exception) {
				it.setRollbackOnly()
				Pair(false, ex.message)
			}
		} ?: Pair(false, null)
	}

	/**
	 * Deletes the default music text channel for a given guild by setting the channel ID to null.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @return The number of rows affected by the update operation.
	 */
	override fun deleteDefaultMusicTextChannel(guildId: Long) =
		jdbcKtTemplateBean.update("UPDATE guilds SET music_text_channel_id = NULL where id = ?", guildId)

	/**
	 * Deletes all settings for a specific guild from the system.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @return The number of rows affected by the delete operation.
	 */
	override fun deleteGuildSettings(guildId: Long) =
		jdbcKtTemplateBean.update("DELETE FROM guilds WHERE discord_id = ?", guildId)
}
