package pl.jwizard.jwc.audio.loader.spinner

import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.command.interaction.MenuOption
import pl.jwizard.jwc.core.util.ext.mdTitleLinkWithDuration
import pl.jwizard.jwc.core.util.ext.titleWithDuration

class TrackMenuOption(val track: Track) : MenuOption {
	override val key
		get() = track.titleWithDuration

	override val value
		get() = track.encoded

	override val formattedToEmbed
		get() = track.mdTitleLinkWithDuration
}
