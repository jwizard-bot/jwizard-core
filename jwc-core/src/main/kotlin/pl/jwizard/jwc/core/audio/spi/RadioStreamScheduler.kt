/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.audio.spi

/**
 * Interface for managing the scheduling of radio streams.
 *
 * The [RadioStreamScheduler] interface extends the [AudioScheduler] and is specifically designed for handling radio
 * streaming functionalities. It defines the necessary properties and methods to manage the playback and control of
 * radio streams within the audio management system.
 *
 * @author Miłosz Gilga
 */
interface RadioStreamScheduler : AudioScheduler {

	/**
	 * Gets the unique slug (identifier) for the radio station.
	 */
	val radioSlug: String
}
