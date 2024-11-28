/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.spi

import java.math.BigInteger

/**
 * Interface for providing module-related data operations.
 *
 * This interface defines methods to interact with and retrieve information on module states, particularly whether
 * specific modules are enabled or disabled for a given guild. Implementations of this interface interact with a
 * persistence layer to manage module states.
 *
 * @author Miłosz Gilga
 */
interface ModuleDataSupplier {

	/**
	 * Retrieves a list of module IDs that are disabled for a specific guild.
	 *
	 * This method is used to fetch all module IDs that are marked as disabled for the given guild, identified by its
	 * database ID. It interacts with the persistence layer to obtain the data.
	 *
	 * @param guildDbId The database ID of the guild for which to retrieve the disabled modules.
	 * @return A list of module IDs that are currently disabled for the specified guild.
	 */
	fun getDisabledGuildModules(guildDbId: BigInteger): List<Long>

	/**
	 * Checks if a particular module is disabled for a specific guild.
	 *
	 * This method determines if a module, identified by its ID, is disabled for the specified guild by querying the
	 * persistence layer. Returns true if the module is disabled, false otherwise.
	 *
	 * @param moduleId The ID of the module to check.
	 * @param guildDbId The database ID of the guild for which to check the module's disabled status.
	 * @return True if the module is disabled for the specified guild, otherwise false.
	 */
	fun isDisabled(moduleId: Long, guildDbId: BigInteger): Boolean
}
