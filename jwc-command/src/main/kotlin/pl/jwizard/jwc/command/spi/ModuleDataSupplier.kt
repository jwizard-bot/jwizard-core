/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.spi

import pl.jwizard.jwc.command.event.ModuleData
import java.math.BigInteger

/**
 * An interface for providing module-related data.
 *
 * Implementations of this interface manage the retrieval of module information and determine whether specific modules
 * or commands are enabled for a given guild.
 *
 * @author Miłosz Gilga
 */
interface ModuleDataSupplier {

	/**
	 * Retrieves a map of all available modules, where the key is the unique module ID and the value is the module name.
	 *
	 * @return A map containing module IDs as keys and their corresponding names as values.
	 */
	fun getModules(): Map<BigInteger, String>

	/**
	 * Checks if a specific command module is enabled for a given guild.
	 *
	 * @param commandName The name of the command to check.
	 * @param guildDbId The unique database ID of the guild.
	 * @return A [ModuleData] object containing module information if the command is enabled for the guild, or `null` if
	 *         the module is disabled.
	 */
	fun isEnabled(commandName: String, guildDbId: BigInteger): ModuleData?
}
