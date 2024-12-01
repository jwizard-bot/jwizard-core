/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild

/**
 * Interface for managing the registration of slash commands in Discord.
 *
 * Implementations of this interface handle the process of registering global and guild-specific slash commands with
 * the Discord API using the JDA library. This ensures that commands are available for users in the appropriate context
 * (global or guild-specific).
 *
 * @author Miłosz Gilga
 */
interface SlashCommandRegisterer {

	/**
	 * Registers all global slash commands for the bot.
	 *
	 * Global commands are available across all Discord servers that the bot has been added to. These commands typically
	 * take longer to propagate due to Discord's caching mechanisms.
	 *
	 * @param jda The [JDA] instance representing the bot's connection to Discord.
	 */
	fun registerGlobalCommands(jda: JDA)

	/**
	 * Registers slash commands specific to a given guild.
	 *
	 * Guild commands are limited to the specified guild and propagate faster than global commands. This method should be
	 * used when commands are tailored for specific guild configurations or requirements.
	 *
	 * @param guild The [Guild] instance representing the Discord server where commands should be registered.
	 */
	fun registerGuildCommands(guild: Guild)
}
