/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.reflect.CommandArgumentDetails
import pl.jwizard.jwc.command.reflect.CommandDetails
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.persistence.sql.JdbcKtTemplateBean
import java.math.BigInteger

/**
 * A Spring component that implements the [CommandDataSupplier] interface. This bean provides functionality to retrieve
 * command-related data from a SQL-based persistence layer using the [JdbcKtTemplateBean].
 *
 * @property jdbcKtTemplateBean A custom template for executing SQL queries and retrieving results.
 * @author Miłosz Gilga
 */
@Component
class CommandDataSupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : CommandDataSupplier {

	/**
	 * Retrieves a map of all commands and their associated metadata from the database.
	 *
	 * @return A map where the key is the command name and the value is a [CommandDetails] object containing detailed
	 * 				 information about the command, including arguments and options.
	 */
	override fun getCommands(): Map<String, CommandDetails> {
		val sql = """
			SELECT
				c.name, c.id, alias, arg argI18nKey, cm.id module, /* command */
				ca.name argName, ca.casted_type type, ca.is_required required, cab.arg_pos position, /* argument */
				cao.option_key optionKey /* argument option */
			FROM bot_commands c
			LEFT JOIN command_arg_options cao ON cao.command_id = c.id
			LEFT JOIN commands_args_binding cab ON cab.command_id = c.id
			LEFT JOIN command_args ca ON ca.id = cab.arg_id 
			INNER JOIN command_modules cm ON cm.id = c.module_id
		""".trimIndent()

		val commands = mutableMapOf<String, CommandDetails>()
		val rawCommands = jdbcKtTemplateBean.queryForList(sql)

		rawCommands.forEach {
			val commandDetails = commands.getOrPut(it["name"] as String) {
				val argI18nKey = it["argI18nKey"]
				CommandDetails(
					id = it["id"] as BigInteger,
					name = it["name"] as String,
					alias = it["alias"] as String,
					argI18nKey = if (argI18nKey != null) argI18nKey as String else null,
					moduleId = it["module"] as BigInteger,
					args = mutableListOf()
				)
			}
			val argName = it["argName"]
			if (argName != null && commandDetails.args.none { arg -> arg.name == argName }) {
				val arg = CommandArgumentDetails(
					name = argName as String,
					type = it["type"] as String,
					required = it["required"] as Boolean,
					position = it["position"] as Long,
					options = mutableListOf()
				)
				commandDetails.args.add(arg)
			}
			val optionKey = it["optionKey"]
			if (optionKey != null) {
				val arg = commandDetails.args.find { arg -> arg.name == argName }
				arg?.options?.add(optionKey as String)
			}
		}
		commands.forEach {
			it.value.args.forEach { arg -> arg.options.sortedBy { option -> option } }
		}
		return commands
	}

	/**
	 * Retrieves a list of enabled command keys for a specific guild, based on whether slash commands are enabled or not.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param slashCommands A flag indicating whether to return slash command keys (`true`) or regular command keys
	 * 				(`false`).
	 * @return A list of command names that are enabled for the specified guild.
	 */
	override fun getEnabledGuildCommandKeys(guildDbId: BigInteger, slashCommands: Boolean): List<String> {
		val sql = jdbcKtTemplateBean.parse(
			"""
				SELECT c.name FROM guilds_commands_binding cb
				INNER JOIN bot_commands c ON cb.command_id = c.id
				WHERE cb.guild_id = ? {{condition}}
			""",
			mapOf("condition" to if (slashCommands) "AND cb.is_slash_enabled = TRUE" else "")
		)
		return jdbcKtTemplateBean.queryForList(sql, String::class.java, guildDbId)
	}

	/**
	 * Retrieves a list of all command argument keys from the database.
	 *
	 * @return A list of strings representing the names of all command arguments.
	 */
	override fun getCommandArgumentKeys(): List<String> =
		jdbcKtTemplateBean.queryForList("SELECT name FROM command_args", String::class.java)

	/**
	 * Retrieves the command properties for a specific guild using its Discord ID.
	 *
	 * @param guildId The unique Discord ID of the guild.
	 * @return A [GuildCommandProperties] object containing command properties for the specified guild, or `null` if no
	 * 				 properties are found.
	 */
	override fun getCommandPropertiesFromGuild(guildId: String): GuildCommandProperties? {
		val sql = """
			SELECT g.id guildDbId, tag lang, legacy_prefix prefix, dj_role_name, slash_enabled
			FROM guilds g
			INNER JOIN bot_langs l ON g.lang_id = l.id
			WHERE discord_id = ?
		""".trimIndent()
		return jdbcKtTemplateBean.queryForDataClass(sql, GuildCommandProperties::class, guildId)
	}

	/**
	 * Checks if a specific command is enabled for a given guild, depending on whether it is a slash command or not.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param commandDbId The unique database ID of the command.
	 * @param slashCommand A flag indicating whether to check for a slash command (`true`) or a regular command (`false`).
	 * @return `true` if the command is enabled for the specified guild, `false` otherwise.
	 */
	override fun isCommandEnabled(guildDbId: BigInteger, commandDbId: BigInteger, slashCommand: Boolean): Boolean {
		val sql = jdbcKtTemplateBean.parse(
			input = "SELECT COUNT(*) > 0 FROM guilds_commands_binding WHERE command_id = ? AND guild_id = ? {{statement}}",
			replacements = mapOf("statement" to if (slashCommand) "AND is_slash_enabled = TRUE" else "")
		)
		return jdbcKtTemplateBean.queryForBool(sql, commandDbId, guildDbId)
	}
}
