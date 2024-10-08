/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.radio

/**
 * Represents a radio station with relevant details.
 *
 * This data class holds information about a radio station including its name, slug, stream URL, proxy stream URL,
 * and an optional cover image.
 *
 * @property name The unique name identifier for the radio station.
 * @property streamUrl The URL for streaming the radio station.
 * @property playbackApiUrl The optional URL for the playback API of the radio station.
 * @property parserClassName The optional name of the class used to parse the playback data.
 * @author Miłosz Gilga
 */
data class RadioStationDetails(
	val name: String,
	val streamUrl: String,
	val playbackApiUrl: String?,
	val parserClassName: String?,
)
