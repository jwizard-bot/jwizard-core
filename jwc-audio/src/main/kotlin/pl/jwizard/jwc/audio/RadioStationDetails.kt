/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

/**
 * Represents the details of a radio station, including its name and streaming URL.
 *
 * @property name The name of the radio station.
 * @property streamUrl The streaming URL of the radio station.
 * @author Miłosz Gilga
 */
data class RadioStationDetails(
	val name: String,
	val streamUrl: String,
)
