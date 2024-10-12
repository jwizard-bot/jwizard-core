/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader.spinner

import dev.arbjerg.lavalink.client.player.Track
import pl.jwizard.jwc.command.interaction.MenuOption
import pl.jwizard.jwc.core.util.ext.mdTitleLinkWithDuration
import pl.jwizard.jwc.core.util.ext.titleWithDuration

/**
 * Represents a menu option for selecting a track. It contains the track's key, value, and a formatted representation
 * to be used in embed messages. The key and formatted string include the track's title and duration.
 *
 * @property track The track associated with this menu option.
 * @author Miłosz Gilga
 */
class TrackMenuOption(val track: Track) : MenuOption {

	/**
	 * The key for this menu option, representing the track's title and its duration.
	 */
	override val key
		get() = track.titleWithDuration

	/**
	 * The value for this menu option, which is the encoded representation of the track.
	 */
	override val value
		get() = track.encoded

	/**
	 * The formatted string for embedding, which includes a markdown-formatted title with a link and the track's duration.
	 */
	override val formattedToEmbed
		get() = track.mdTitleLinkWithDuration
}
