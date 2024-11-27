/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.sql

import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.persistence.sql.JdbiQueryBean
import java.math.BigInteger

/**
 * An IoC component that implements the [ModuleDataSupplier] interface. This bean provides functionality for
 * retrieving module-related data from a SQL-based persistence layer using the [JdbiQueryBean].
 *
 * @property jdbiQuery Bean for executing SQL queries.
 * @author Miłosz Gilga
 */
@SingletonComponent
class ModuleDataSupplierBean(private val jdbiQuery: JdbiQueryBean) : ModuleDataSupplier {

	/**
	 * Retrieves a list of module IDs that are disabled for a specific guild.
	 *
	 * This method executes an SQL query to fetch all module IDs that have been marked as disabled for the specified
	 * guild ID from the `guilds_disabled_modules` table.
	 *
	 * @param guildDbId The database ID of the guild for which to retrieve disabled modules.
	 * @return A list of module IDs that are disabled for the given guild.
	 */
	override fun getDisabledGuildModules(guildDbId: BigInteger): List<Long> {
		val sql = "SELECT module_id FROM guilds_disabled_modules WHERE guild_id = ?"
		return jdbiQuery.queryForList(sql, Long::class, guildDbId)
	}

	/**
	 * Checks if a specific module is disabled for a given guild.
	 *
	 * Executes an SQL query to determine if the specified module ID is disabled for the provided guild ID in the
	 * `guilds_disabled_modules` table. Returns true if the module is disabled, otherwise false.
	 *
	 * @param moduleId The ID of the module to check.
	 * @param guildDbId The database ID of the guild to check for the module's status.
	 * @return True if the module is disabled for the given guild; false otherwise.
	 */
	override fun isDisabled(moduleId: Long, guildDbId: BigInteger): Boolean {
		val sql = "SELECT COUNT(*) > 0 FROM guilds_disabled_modules WHERE module_id = ? AND guild_id = ?"
		return jdbiQuery.queryForBool(sql, moduleId, guildDbId)
	}
}
