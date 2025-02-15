package pl.jwizard.jwc.persistence.sql

import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbiQueryBean
import java.math.BigInteger

@SingletonComponent
class CommandDataSupplierBean(private val jdbiQuery: JdbiQueryBean) : CommandDataSupplier {
	override fun getDisabledGuildCommands(
		guildDbId: BigInteger,
		slashCommands: Boolean,
	): List<Long> {
		val sql = jdbiQuery.parse(
			input = """
				SELECT command_id FROM guilds_disabled_commands
				WHERE guild_id = ? AND {{disabledColName}} = TRUE
			""",
			replacements = mapOf(
				"disabledColName" to if (slashCommands) "slash_disabled " else "prefix_disabled"
			)
		)
		return jdbiQuery.queryForList(sql, Long::class, guildDbId)
	}

	override fun isCommandDisabled(
		guildDbId: BigInteger,
		commandId: Long,
		slashCommand: Boolean,
	): Boolean {
		val sql = jdbiQuery.parse(
			input = """
				SELECT COUNT(*) > 0 FROM guilds_disabled_commands
				WHERE command_id = ? AND guild_id = ? AND {{disabledColName}} = TRUE
			""",
			replacements = mapOf(
				"disabledColName" to if (slashCommand) "slash_disabled" else "prefix_disabled"
			),
		)
		return jdbiQuery.queryForBool(sql, commandId, guildDbId)
	}
}
