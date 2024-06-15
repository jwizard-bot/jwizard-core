/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db.sql

import net.dv8tion.jda.api.entities.Guild
import org.apache.commons.lang3.StringUtils
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import pl.jwizard.core.bot.properties.BotProperties
import pl.jwizard.core.db.*
import pl.jwizard.core.jdbc.JdbcUtils.parse
import pl.jwizard.core.log.AbstractLoggingBean
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement
import kotlin.reflect.KClass

@Service
class GuildSettingsSqlSupplier(
	private val jdbcTemplate: JdbcTemplate,
	private val botProperties: BotProperties,
	private val transactionTemplate: TransactionTemplate,
) : AbstractLoggingBean(GuildSettingsSqlSupplier::class), GuildSettingsSupplier {

	override fun persistGuildSettings(guild: Guild) {
		val alreadyExist = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) > 0 FROM guilds WHERE discord_id = ?",
			Boolean::class.java, guild.id
		)
		if (alreadyExist) {
			log.info("Guild settings for guild: {} already persisted, skipping initialization.", guild.name)
			return
		}
		transactionTemplate.execute {
			try {
				val plLangId = jdbcTemplate.queryForObject(
					"SELECT id FROM bot_langs WHERE tag = ?",
					Long::class.java, botProperties.instance.defaultLanguage
				) as Long? ?: throw RuntimeException("Could not find default language")

				val keyHolder = GeneratedKeyHolder()
				jdbcTemplate.update({ connetion: Connection ->
					val ps = connetion.prepareStatement(
						"INSERT INTO guilds(discord_id, lang_id) VALUES (?, ?)",
						Statement.RETURN_GENERATED_KEYS
					)
					ps.setString(1, guild.id)
					ps.setLong(2, plLangId)
					ps
				}, keyHolder)
				val generatedId = keyHolder.key ?: throw java.lang.RuntimeException("Could not find generated ID")
				insertIndexesToDataTable("command_modules", "guilds_modules_binding", "module_id", generatedId.toLong())
				insertIndexesToDataTable("bot_commands", "guilds_commands_binding", "command_id", generatedId.toLong())

				log.info("Successfully persisted new guild.")
			} catch (ex: Exception) {
				log.error("Unexpected error on persisting guild settings. Cause: {}. Leaving guild: {}.", ex.message, guild.id)
				it.setRollbackOnly()
				guild.leave().queue()
			}
		}
	}

	private fun insertIndexesToDataTable(sizeTableName: String, tableName: String, colName: String, guildId: Long) {
		val totalIndexes = jdbcTemplate.queryForList(
			parse("SELECT id FROM {{tableName}}", mapOf("tableName" to sizeTableName)),
			Long::class.java
		)
		val batchSql = parse(
			"INSERT INTO {{tableName}}(guild_id, {{colName}}) VALUES (?, ?)",
			mapOf("tableName" to tableName, "colName" to colName)
		)
		jdbcTemplate.batchUpdate(batchSql, object : BatchPreparedStatementSetter {
			override fun setValues(ps: PreparedStatement, i: Int) {
				ps.setLong(1, guildId)
				ps.setLong(2, totalIndexes[i])
			}

			override fun getBatchSize(): Int = totalIndexes.size
		})
	}

	override fun removeDefaultMusicTextChannel(guild: Guild) {
		jdbcTemplate.update("UPDATE guilds SET music_text_channel_id = NULL WHERE id = ?", guild.id)
		log.info("Removed music text channel id from guild: {}.", guild.name)
	}

	override fun deleteGuildSettings(guild: Guild) {
		jdbcTemplate.update("DELETE FROM guilds WHERE discord_id = ?", guild.id)
		log.info("Successfully deleted guild settings for guild: {}.", guild.name)
	}

	override fun <T : Any> fetchDbProperty(property: GuildDbProperty, guildId: String, clazz: KClass<T>): T {
		val jClazz = clazz.java
		val sql = parse(
			"SELECT {{columnName}} FROM guilds WHERE discord_id = ?",
			mapOf("columnName" to property.columnName)
		)
		val nullableProp = jdbcTemplate.queryForObject(sql, jClazz, guildId) as T?
		if (nullableProp == null) {
			log.debug(
				"Property for column: {} not found. Returning default value: {}.", property.columnName,
				property.defaultValue
			)
			return jClazz.cast(property.defaultValue)
		}
		log.debug("Fetch property column: {} with value: {}.", property.columnName, nullableProp)
		return nullableProp
	}

	override fun fetchGuildCommandProperties(guildId: String): GuildCommandPropertiesDto? {
		val sql = """
			SELECT g.id AS id, tag AS lang, legacy_prefix AS prefix, dj_role_name, slash_enabled FROM guilds AS g
			INNER JOIN bot_langs AS l ON g.lang_id = l.id WHERE discord_id = ?
			""".trimIndent()
		val properties = jdbcTemplate
			.queryForObject(sql, DataClassRowMapper(GuildCommandPropertiesDto::class.java), guildId)
		log.debug("Successfully fetched guild command properties: {}", properties)
		return properties
	}

	override fun checkIfCommandIsEnabled(guildId: Long, commandId: Long, isSlashCommand: Boolean): Boolean {
		val sql = parse(
			"SELECT COUNT(*) > 0 FROM guilds_commands_binding WHERE command_id = ? AND guild_id = ? {{statement}}",
			mapOf("statement" to if (isSlashCommand) "AND is_slash_enabled = TRUE" else StringUtils.EMPTY)
		)
		return jdbcTemplate.queryForObject(sql, Boolean::class.java, commandId, guildId)
	}

	override fun fetchVotingSongChooserSettings(guildId: Long): VotingSongChooserSettings {
		val sql = """
			SELECT random_auto_choose_track, time_after_auto_choose_sec, tracks_to_choose_max
			FROM guilds WHERE id = ?
			""".trimIndent()
		val result = jdbcTemplate.queryForObject(sql, DataClassRowMapper(VotingSongChooserSettings::class.java), guildId)
		if (result == null) {
			val defValues = VotingSongChooserSettings()
			log.debug("Properties for song chooser settings not found. Returning default value: {}.", defValues)
			return defValues
		}
		log.debug("Successfully fetched song chooser settings: {}", result)
		return result
	}

	override fun fetchGuildLang(guildId: String): String {
		val sql = "SELECT tag FROM guilds AS g INNER JOIN bot_langs AS l ON g.lang_id = l.id WHERE discord_id = ?"
		val result = jdbcTemplate.queryForObject(sql, String::class.java, guildId)
		if (result.isEmpty()) {
			val defValue = botProperties.instance.defaultLanguage
			log.debug("Persisted guild language not found. Returning default value: {}.", defValue)
			return defValue
		}
		log.debug("Successfully fetched guild language: {}", result)
		return result
	}

	override fun fetchGuildCombinedProperties(guildId: String): GuildCombinedPropertiesDto? {
		val sql = """
			SELECT
				voting_percentage_ratio,
				time_to_finish_voting_sec,
				music_text_channel_id,
				max_repeats_of_track,
				leave_empty_channel_sec,
				leave_no_tracks_channel_sec,
				default_volume,
				random_auto_choose_track,
				time_after_auto_choose_sec,
				tracks_to_choose_max
			FROM guilds WHERE discord_id = ?
		""".trimIndent()
		return jdbcTemplate.queryForObject(sql, DataClassRowMapper(GuildCombinedPropertiesDto::class.java), guildId)
	}
}
