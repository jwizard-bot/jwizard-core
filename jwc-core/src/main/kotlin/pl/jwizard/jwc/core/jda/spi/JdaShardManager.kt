/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.managers.DirectAudioController

/**
 * Interface for managing shards in a JDA (Java Discord API) bot.
 *
 * This interface provides methods to interact with multiple shards, manage bot presence, and access important JDA
 * resources like guilds, users, and audio controllers.
 *
 * @author Miłosz Gilga
 */
interface JdaShardManager {

	/**
	 * The total number of shards currently queued for processing.
	 */
	val queuedShardsCount: Int

	/**
	 * The total number of shards that are currently running.
	 */
	val runningShardsCount: Int

	/**
	 * The average gateway ping across all active shards.
	 */
	val averageGatewayPing: Double

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
	fun getGuildById(guildId: Long): Guild?

	/**
	 * Retrieves a [User] by its unique identifier.
	 *
	 * This method fetches a user object based on the user's unique ID, allowing further interactions with that user,
	 * such as sending messages or performing actions.
	 *
	 * @param userId The unique identifier of the user to retrieve.
	 * @return The [User] object representing the user with the specified ID, or null if the user is not found.
	 */
	fun getUserById(userId: Long): User?

	/**
	 * Retrieves the [DirectAudioController] for a specific guild.
	 *
	 * This method returns the DirectAudioController for a particular guild, which is responsible for handling voice
	 * connections and audio features in the guild. It is used to control the bot’s voice chat in a guild.
	 *
	 * @param guild The guild for which the audio controller is required.
	 * @return The [DirectAudioController] for the specified guild, or null if the controller is not available.
	 */
	fun getDirectAudioController(guild: Guild): DirectAudioController?
}
