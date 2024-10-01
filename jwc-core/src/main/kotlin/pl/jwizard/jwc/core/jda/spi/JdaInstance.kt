/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.spi

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.managers.DirectAudioController

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
	 * Accesses the [DirectAudioController] for managing audio playback directly in voice channels.
	 *
	 * This property provides an interface to control audio playback, allowing the bot to join and leave voice channels,
	 * play audio, and manage audio-related tasks.
	 */
	val directAudioController: DirectAudioController
}
