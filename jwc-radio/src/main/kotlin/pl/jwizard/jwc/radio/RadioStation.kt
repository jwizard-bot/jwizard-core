/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

/**
 * Represents a radio station with relevant details.
 *
 * This data class holds information about a radio station including its name, slug, stream URL, proxy stream URL,
 * and an optional cover image.
 *
 * @property name The name of the radio station.
 * @property slug The unique slug identifier for the radio station.
 * @property streamUrl The URL for streaming the radio station.
 * @property proxyStreamUrl The URL for proxy streaming the radio station.
 * @property coverImage The optional URL of the cover image for the radio station.
 * @author Miłosz Gilga
 */
data class RadioStation(
	val name: String,
	val slug: String,
	val streamUrl: String,
	val proxyStreamUrl: String,
	val coverImage: String?,
)
