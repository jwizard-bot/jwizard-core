/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql.bind

import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.persistence.sql.JdbcKtTemplateBean
import pl.jwizard.jwc.persistence.sql.SqlColumn
import pl.jwizard.jwl.i18n.spi.LanguageSupplier
import pl.jwizard.jwl.property.AppBaseProperty.*
import pl.jwizard.jwl.property.AppProperty
import java.sql.JDBCType.*

/**
 * Component responsible for managing guild settings, including creation, deletion, and retrieval.
 *
 * @property jdbcKtTemplateBean Bean for executing SQL queries.
 * @property translationTemplate Template for handling transactions.
 * @property languageSupplier Supplier providing language support for guild settings.
 * @property environmentBean Bean for fetching environment properties.
 * @author Miłosz Gilga
 */
@Component
class GuildSettingsEventActionBean(
	private val jdbcKtTemplateBean: JdbcKtTemplateBean,
	private val translationTemplate: TransactionTemplate,
	private val languageSupplier: LanguageSupplier,
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
			"SELECT COUNT(*) > 0 FROM guilds WHERE discord_id = ?",
			guildId
		)
		if (guildSettingsAlreadyExist) {
			return Pair(false, null)
		}
		val languageTag = guildLocale.substring(0, 2)
		return translationTemplate.execute {
			try {
				val columns = mapOf(
					"discord_id" to SqlColumn(guildId, BIGINT),
					"legacy_prefix" to SqlColumn(getProperty(GUILD_DEFAULT_LEGACY_PREFIX), CHAR),
					"lang_id" to SqlColumn(languageSupplier.getLanguageId(languageTag), BIGINT),
					"dj_role_name" to SqlColumn(getProperty(GUILD_DJ_ROLE_NAME), VARCHAR),
					"slash_enabled" to SqlColumn(getProperty(GUILD_DEFAULT_SLASH_ENABLED), BOOLEAN),
					"leave_empty_channel_sec" to SqlColumn(getProperty(GUILD_LEAVE_EMPTY_CHANNEL_SEC), INTEGER),
					"leave_no_tracks_channel_sec" to SqlColumn(getProperty(GUILD_LEAVE_NO_TRACKS_SEC), INTEGER),
					"voting_percentage_ratio" to SqlColumn(getProperty(GUILD_VOTING_PERCENTAGE_RATIO), INTEGER),
					"time_to_finish_voting_sec" to SqlColumn(getProperty(GUILD_MAX_VOTING_TIME_SEC), INTEGER),
					"random_auto_choose_track" to SqlColumn(getProperty(GUILD_RANDOM_AUTO_CHOOSE_TRACK), BOOLEAN),
					"tracks_to_choose_max" to SqlColumn(getProperty(GUILD_MAX_TRACKS_TO_CHOOSE), INTEGER),
					"time_after_auto_choose_sec" to SqlColumn(getProperty(GUILD_TIME_AFTER_AUTO_CHOOSE_SEC), INTEGER),
					"max_repeats_of_track" to SqlColumn(getProperty(GUILD_MAX_REPEATS_OF_TRACK), INTEGER),
					"default_volume" to SqlColumn(getProperty(GUILD_DEFAULT_VOLUME), INTEGER),
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

	/**
	 * Retrieves settings for a specific guild from the database.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @return A map containing the guild's settings, where the keys are instances of [GuildProperty] and the values
	 *         are the associated settings.
	 */
	override fun getGuildSettings(guildId: Long): Map<GuildProperty, Any?> {
		val fetchedProperties = jdbcKtTemplateBean.queryForMap("SELECT * FROM guilds WHERE discord_id = ?", guildId)
		val combinedProperties = mutableMapOf<GuildProperty, Any?>()
		fetchedProperties.forEach {
			GuildProperty.entries.find { entry -> entry.key == it.key }?.let { property ->
				combinedProperties[property] = it.value
			}
		}
		return combinedProperties.toMap()
	}

	/**
	 * Retrieves a property from the environment based on the given [AppProperty].
	 *
	 * @param T The type of the property to retrieve.
	 * @param botProperty The bot property whose value should be fetched from the environment.
	 * @return The value of the specified property cast to the appropriate type.
	 */
	private inline fun <reified T : Any> getProperty(botProperty: AppProperty) =
		environmentBean.getProperty<T>(botProperty)
}
