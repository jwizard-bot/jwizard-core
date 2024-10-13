/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import dev.arbjerg.lavalink.client.player.Track
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder

/**
 * Interface for managing music playback and related operations within the audio system.
 *
 * The [MusicManager] interface encapsulates functionalities for controlling music playback, handling player states,
 * and facilitating interaction with audio tracks and streams. It serves as a bridge between the audio state management
 * and the actual music player operations.
 *
 * @author Miłosz Gilga
 */
interface MusicManager {

	/**
	 * Gets the audio state manager that oversees the current audio playback state.
	 */
	val state: AudioStateManager

	/**
	 * Gets the player update builder used for creating or updating the audio player state.
	 */
	val createdOrUpdatedPlayer: PlayerUpdateBuilder

	/**
	 * Gets the currently cached Lavalink player instance.
	 */
	val cachedPlayer: LavalinkPlayer?

	/**
	 * Retrieves the audio sender ID associated with a given track.
	 *
	 * This method can be used to identify the user or entity that sent the request to play the specified track.
	 *
	 * @param track The track for which to retrieve the sender ID.
	 * @return The ID of the audio sender, or `null` if not available.
	 */
	fun getAudioSenderId(track: Track?): Long?

	/**
	 * Loads a track by its name and starts playing it.
	 *
	 * This method will handle the process of loading the specified track and initiating playback in the audio player.
	 *
	 * @param trackName The name of the track to load and play.
	 * @param context The command context providing information about the command execution.
	 */
	fun loadAndPlay(trackName: String, context: CommandBaseContext)

	/**
	 * Loads a radio stream by its name and URL, then starts streaming it.
	 *
	 * This method handles the loading of a radio stream and begins playback in the audio player.
	 *
	 * @param name The name of the radio station or stream.
	 * @param streamUrl The URL of the stream to be loaded and played.
	 * @param context The command context providing information about the command execution.
	 */
	fun loadAndStream(name: String, streamUrl: String, context: CommandBaseContext)

	/**
	 * Creates a new embed builder for constructing message embeds.
	 *
	 * This method provides a way to create a customized embed builder for sending messages in the Discord interface.
	 *
	 * @return A new instance of `MessageEmbedBuilder`.
	 */
	fun createEmbedBuilder(): MessageEmbedBuilder

	/**
	 * Sends a message embed to the appropriate channel or context, optionally including interactive components like
	 * action rows.
	 *
	 * @param message The message embed to be sent.
	 * @param actionRows Optional interactive components to be included with the message.
	 */
	fun sendMessage(message: MessageEmbed, vararg actionRows: ActionRow)

	/**
	 * Cleans up resources and disposes of the music manager.
	 *
	 * This method should be called when the music manager is no longer needed to release any allocated resources.
	 */
	fun dispose()
}
