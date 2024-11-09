/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbcKtTemplateBean
import java.math.BigInteger

/**
 * An IoC component that implements the [CommandDataSupplier] interface. This bean provides functionality to retrieve
 * command-related data from a SQL-based persistence layer using the [JdbcKtTemplateBean].
 *
 * @property jdbcKtTemplateBean A custom template for executing SQL queries and retrieving results.
 * @author Miłosz Gilga
 */
@SingletonComponent
class CommandDataSupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : CommandDataSupplier {

	/**
	 * Retrieves a list of disabled command Ids for a specific guild, based on whether slash commands are enabled or not.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param slashCommands A flag indicating whether to return slash command keys (`true`) or regular command keys
	 *        (`false`).
	 * @return A list of command Ids that are disabled for the specified guild.
	 */
	override fun getDisabledGuildCommands(guildDbId: BigInteger, slashCommands: Boolean): List<Long> {
		val sql = jdbcKtTemplateBean.parse(
			input = """
				SELECT command_id FROM guilds_disabled_commands
				WHERE guild_id = ? AND {{disabledColName}} = TRUE
			""",
			replacements = mapOf("disabledColName" to if (slashCommands) "slash_disabled " else "prefix_disabled")
		)
		return jdbcKtTemplateBean.queryForList(sql, Long::class.java, guildDbId)
	}

	/**
	 * Checks if a specific command is disabled for a given guild, depending on whether it is a slash command or not.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param commandId The unique ID of the command.
	 * @param slashCommand A flag indicating whether to check for a slash command (`true`) or a regular command (`false`).
	 * @return `true` if the command is disabled for the specified guild, `false` otherwise.
	 */
	override fun isCommandDisabled(guildDbId: BigInteger, commandId: Long, slashCommand: Boolean): Boolean {
		val sql = jdbcKtTemplateBean.parse(
			input = """
				SELECT COUNT(*) > 0 FROM guilds_disabled_commands
				WHERE command_id = ? AND guild_id = ? AND {{disabledColName}} = TRUE
			""",
			replacements = mapOf("disabledColName" to if (slashCommand) "slash_disabled" else "prefix_disabled"),
		)
		return jdbcKtTemplateBean.queryForBool(sql, commandId, guildDbId)
	}
}
