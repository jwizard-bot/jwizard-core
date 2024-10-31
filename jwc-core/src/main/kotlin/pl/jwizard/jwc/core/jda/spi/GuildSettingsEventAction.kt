/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

/**
 * Interface defining actions related to guild settings, such as creating, deleting, and retrieving settings for a
 * specific guild.
 *
 * @author Miłosz Gilga
 */
interface GuildSettingsEventAction {

	/**
	 * Creates settings for a specific guild based on the provided guild ID and locale. If the guild settings already
	 * exist, it returns false without creating new settings.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @param guildLocale Locale used by the guild for language settings.
	 * @return A pair where the first value is true if the settings were created successfully, and the second value
	 *         contains an error message if the creation failed.
	 */
	fun createGuildSettings(guildId: Long, guildLocale: String): Pair<Boolean, String?>

	/**
	 * Deletes the default music text channel for a given guild by setting the channel ID to null.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @return The number of rows affected by the update operation.
	 */
	fun deleteDefaultMusicTextChannel(guildId: Long): Int

	/**
	 * Deletes all settings for a specific guild from the persistent storage.
	 *
	 * @param guildId Unique identifier of the guild.
	 * @return The number of rows affected by the delete operation.
	 */
	fun deleteGuildSettings(guildId: Long): Int
}
