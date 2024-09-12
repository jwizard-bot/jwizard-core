/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.entities.Guild

/**
 * Interface for interacting with the JDA (Java Discord API) bean instance.
 *
 * @author Miłosz Gilga
 */
interface JdaInstance {

	/**
	 * Sets the presence activity of the bot.
	 *
	 * This method allows the bot to update its activity status, such as setting a custom status message or activity
	 * type. The activity is represented by a string that will be displayed as the bot's presence.
	 *
	 * @param activity A string representing the new activity status to set for the bot.
	 */
	fun setPresenceActivity(activity: String)

	/**
	 * Retrieves a [Guild] by its unique identifier.
	 *
	 * @param guildId The unique identifier of the guild to retrieve.
	 * @return The [Guild] object representing the guild with the specified ID, or null if the guild is not found.
	 */
	fun getGuildById(guildId: String): Guild?
}
