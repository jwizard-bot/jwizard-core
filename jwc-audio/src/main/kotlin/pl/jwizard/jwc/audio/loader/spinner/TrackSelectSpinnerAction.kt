/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader.spinner

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwac.player.track.Track

/**
 * Defines actions related to selecting and managing tracks from a spinner menu. It provides methods for handling track
 * queuing and creating response messages for the selected track.
 *
 * @author Miłosz Gilga
 */
interface TrackSelectSpinnerAction {

	/**
	 * Handles the action of enqueuing a selected track to the playlist or queue.
	 *
	 * @param track The track to be enqueued.
	 */
	fun onEnqueueTrack(track: Track)

	/**
	 * Creates a message embed to provide a response when a track is selected. This method generates an embedded message
	 * displaying the track details, such as its title and duration.
	 *
	 * @param track The track for which the response message will be created.
	 * @return A [MessageEmbed] containing the track information formatted for display.
	 */
	fun createTrackResponseMessage(track: Track): MessageEmbed
}
