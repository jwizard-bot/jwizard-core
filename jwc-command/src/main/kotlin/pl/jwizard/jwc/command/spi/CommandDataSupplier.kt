/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.spi

import java.math.BigInteger

/**
 * Interface for managing command-specific data operations for guilds.
 *
 * This interface defines methods to retrieve and check the status of commands, specifically for handling disabled
 * commands on a per-guild basis. It supports both slash commands and regular commands, allowing the caller to specify
 * the command type as needed.
 *
 * @author Miłosz Gilga
 */
interface CommandDataSupplier {

	/**
	 * Retrieves a list of disabled command Ids for a specific guild, depending on whether slash commands are enabled
	 * or not.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param slashCommands A flag indicating whether to fetch slash command keys (`true`) or regular command keys
	 *        (`false`).
	 * @return A list of command Ids that are disabled for the specified guild.
	 */
	fun getDisabledGuildCommands(guildDbId: BigInteger, slashCommands: Boolean): List<Long>

	/**
	 * Checks whether a specific command is disabled for a given guild.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param commandId The unique ID of the command.
	 * @param slashCommand A flag indicating whether to check for a slash command (`true`) or a regular command (`false`).
	 * @return `true` if the command is disabled for the specified guild, `false` otherwise.
	 */
	fun isCommandDisabled(guildDbId: BigInteger, commandId: Long, slashCommand: Boolean): Boolean
}
