/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.spi

import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.reflect.CommandDetails
import java.math.BigInteger

/**
 * An interface that defines methods for supplying command-related data.
 *
 * Implementations of this interface are responsible for providing command metadata, enabling and disabling commands for
 * specific guilds, and retrieving guild-specific command settings.
 *
 * @author Miłosz Gilga
 */
interface CommandDataSupplier {

	/**
	 * Retrieves a map of all commands with their associated metadata.
	 *
	 * @return A map where the key is the command name and the value is the [CommandDetails] object containing metadata
	 *         about the command.
	 */
	fun getCommands(): Map<String, CommandDetails>

	/**
	 * Retrieves a list of enabled command keys for a specific guild, depending on whether slash commands are enabled
	 * or not.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param slashCommands A flag indicating whether to fetch slash command keys (`true`) or regular command keys
	 * 				(`false`).
	 * @return A list of command keys that are enabled for the specified guild.
	 */
	fun getEnabledGuildCommandKeys(guildDbId: BigInteger, slashCommands: Boolean): List<String>

	/**
	 * Retrieves a list of keys for all available command arguments.
	 *
	 * @return A list of strings representing the available command argument keys.
	 */
	fun getCommandArgumentKeys(): List<String>

	/**
	 * Retrieves the command properties for a specific guild based on its ID.
	 *
	 * @param guildId The unique ID of the guild.
	 * @return A [GuildCommandProperties] object containing the guild's command settings, or `null` if no properties are
	 * 				 found for the specified guild.
	 */
	fun getCommandPropertiesFromGuild(guildId: String): GuildCommandProperties?

	/**
	 * Checks whether a specific command is enabled for a given guild.
	 *
	 * @param guildDbId The unique database ID of the guild.
	 * @param commandDbId The unique database ID of the command.
	 * @param slashCommand A flag indicating whether to check for a slash command (`true`) or a regular command (`false`).
	 * @return `true` if the command is enabled for the specified guild, `false` otherwise.
	 */
	fun isCommandEnabled(guildDbId: BigInteger, commandDbId: BigInteger, slashCommand: Boolean): Boolean
}
