/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db.sql

import org.apache.commons.lang3.StringUtils
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import pl.jwizard.core.command.reflect.CommandArgDto
import pl.jwizard.core.command.reflect.CommandArgOptionDto
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.command.reflect.ModuleDetailsDto
import pl.jwizard.core.db.CommandsSupplier
import pl.jwizard.core.jdbc.JdbcUtils.parse
import pl.jwizard.core.jdbc.JdbcUtils.parseToLong

@Service
class CommandsSqlSupplier(
	private val jdbcTemplate: JdbcTemplate,
) : CommandsSupplier {

	override fun fetchAllModules(): Map<String, ModuleDetailsDto> {
		val commandModules = jdbcTemplate.queryForList("SELECT * FROM command_modules")
		val resultMap = mutableMapOf<String, ModuleDetailsDto>()
		for (row in commandModules) {
			resultMap[row["name"] as String] = ModuleDetailsDto(
				parseToLong(row["id"]),
				parseColumnsToMultiLangMap(row, "desc_")
			)
		}
		return resultMap
	}

	override fun fetchAllCommands(): Map<String, CommandDetailsDto> {
		val langs = jdbcTemplate.queryForList("SELECT tag FROM bot_langs", String::class.java)
		val resultMap = mutableMapOf<String, CommandDetailsDto>()

		val argsSql = parse(
			"""
				SELECT a.id AS aId, ca.command_id AS cId, a.name AS aName, casted_type, is_required, arg_pos, {{aDSqlF}}
				FROM commands_args_binding AS ca INNER JOIN command_args AS a ON ca.arg_id = a.id
			""".trimIndent(),
			mapOf("aDSqlF" to langs.joinToString(transform = { "desc_$it" }, separator = ", ")),
		)
		val argOptionsSql = parse(
			"SELECT raw_value, {{aDSqlF}}, command_id, command_arg_id FROM command_arg_options",
			mapOf("aDSqlF" to langs.joinToString(transform = { "desc_$it" }, separator = ", ")),
		)
		val commandsSql = parse(
			"""
				SELECT c.id AS cId, c.name AS cName, c.alias AS cAlias, m.name AS cModule, {{aDSqlF}}, {{cDSqlF}}
				FROM bot_commands AS c INNER JOIN command_modules AS m ON c.module_id = m.id
			""".trimIndent(),
			mapOf(
				"aDSqlF" to langs.joinToString(transform = { "arg_desc_$it" }, separator = ", "),
				"cDSqlF" to langs.joinToString(transform = { "c.desc_$it" }, separator = ", "),
			),
		)
		val argOptionsMap = jdbcTemplate.queryForList(argOptionsSql)
			.groupBy { parseToLong(it["command_arg_id"]) }
			.map { (key, value) ->
				key to value.map {
					CommandArgOptionDto(
						it["raw_value"] as String,
						parseColumnsToMultiLangMap(it, "desc_"),
						parseToLong(it["command_id"]),
					)
				}
			}.toMap()

		val argsMap = jdbcTemplate.queryForList(argsSql)
			.groupBy { parseToLong(it["cId"]) }
			.map { (key, value) ->
				key to value.map {
					CommandArgDto(
						it["aName"] as String,
						parseColumnsToMultiLangMap(it, "desc_"),
						it["casted_type"] as String,
						it["is_required"] as Boolean,
						it["arg_pos"] as Long,
						argOptionsMap[parseToLong(it["aId"])]?.filter { option -> option.commandId == key } ?: emptyList()
					)
				}
			}.toMap()

		val botCommands = jdbcTemplate.queryForList(commandsSql)
		for (row in botCommands) {
			val commandId = parseToLong(row["cId"])
			val name = row["cName"] as String
			resultMap[name] = CommandDetailsDto(
				commandId,
				name,
				row["cAlias"] as String,
				row["cModule"] as String,
				parseColumnsToMultiLangMap(row, "desc_"),
				parseColumnsToMultiLangMap(row, "arg_desc_"),
				argsMap[commandId]?.sortedBy { it.pos } ?: emptyList()
			)
		}
		return resultMap
	}

	override fun checkIfModuleIsEnabled(moduleName: String, guildId: Long): Boolean = jdbcTemplate.queryForObject(
		"SELECT COUNT(*) > 0 FROM guilds_modules_binding AS gm WHERE guild_id = ?",
		Boolean::class.java,
		guildId
	)

	override fun fetchEnabledGuildCommands(guildId: Long, isSlashCommands: Boolean): List<String> {
		val sql = parse(
			"""
				SELECT c.name FROM guilds_commands_binding AS cb
				INNER JOIN bot_commands AS c ON cb.command_id = c.id
				WHERE cb.guild_id = ? {{statement}}
			""".trimIndent(),
			mapOf("statement" to if (isSlashCommands) "AND cb.is_slash_enabled = TRUE" else StringUtils.EMPTY)
		)
		return jdbcTemplate.queryForList(sql, String::class.java, guildId)
	}

	override fun fetchAllCommandArguments(): List<String> = jdbcTemplate.queryForList(
		"SELECT name FROM command_args",
		String::class.java
	)

	private fun parseColumnsToMultiLangMap(
		row: MutableMap<String, Any>,
		columnName: String,
	): Map<String, String?> = row.entries
		.filter { it.key.contains(columnName) }
		.associate { (key, value) -> key.replace(columnName, StringUtils.EMPTY) to value as? String }
}
