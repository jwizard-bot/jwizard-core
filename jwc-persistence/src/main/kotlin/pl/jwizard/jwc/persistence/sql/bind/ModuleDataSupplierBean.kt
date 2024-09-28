/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.event.ModuleData
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.persistence.sql.ColumnDef
import pl.jwizard.jwc.persistence.sql.JdbcKtTemplateBean
import java.math.BigInteger

/**
 * A Spring component that implements the [ModuleDataSupplier] interface. This bean provides functionality for
 * retrieving module-related data from a SQL-based persistence layer using the [JdbcKtTemplateBean].
 *
 * @property jdbcKtTemplateBean A custom template for executing SQL queries and retrieving results.
 * @author Miłosz Gilga
 */
@Component
class ModuleDataSupplierBean(private val jdbcKtTemplateBean: JdbcKtTemplateBean) : ModuleDataSupplier {

	/**
	 * Retrieves a map of module IDs and names from the database.
	 *
	 * @return A map where the key is the module ID ([BigInteger]) and the value is the module name ([String]).
	 */
	override fun getModules() = jdbcKtTemplateBean.queryForListMap(
		"SELECT id, name FROM command_modules",
		ColumnDef("id", BigInteger::class),
		ColumnDef("name", String::class),
	)

	/**
	 * Checks if a module associated with a specific command is enabled for a given guild.
	 *
	 * @param commandName The name of the command for which to check module status.
	 * @param guildDbId The unique database ID of the guild.
	 * @return A [ModuleData] object containing the module name and its activation status (`isActive`), or `null` if no
	 *         module is found.
	 */
	override fun isEnabled(commandName: String, guildDbId: BigInteger): ModuleData? {
		val sql = """
			SELECT cm.name, COUNT(gmb.guild_id) > 0 isActive FROM bot_commands bc
			LEFT JOIN command_modules cm ON cm.id = bc.module_id
			LEFT JOIN guilds_modules_binding gmb ON gmb.module_id = cm.id AND gmb.guild_id = ?
			WHERE bc.name = ?
		""".trimIndent()
		return jdbcKtTemplateBean.queryForDataClass(sql, ModuleData::class, guildDbId, commandName)
	}
}
