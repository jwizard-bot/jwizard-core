/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.entities.Guild

/**
 * Interface for registering slash commands in a Discord guild.
 *
 * Implementing classes should provide the logic to register commands specific to a guild, enabling features that
 * depend on slash commands.
 *
 * @author Miłosz Gilga
 */
interface SlashCommandRegisterer {

	/**
	 * Registers slash commands for the specified guild.
	 *
	 * This method is called to initialize and register all necessary commands for the provided guild. Implementations
	 * should handle the specifics of the registration process, including any necessary error handling or logging.
	 *
	 * @param guild The guild where the commands will be registered.
	 */
	fun registerGuildCommands(guild: Guild)
}
