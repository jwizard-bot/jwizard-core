package pl.jwizard.jwc.audio.loader.spinner

import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.command.interaction.MenuOption

internal class TrackMenuOption(val track: Track) : MenuOption {
	override val key
		get() = track.titleWithDuration

	override val value
		get() = track.encoded

	override val formattedToEmbed
		get() = track.mdTitleLinkWithDuration
}
