/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler.repeat

/**
 * Class for managing the count of repeats for an audio track or playlist. This class keeps track of the current number
 * of repeats and the total number of repeats allowed.
 *
 * @author Miłosz Gilga
 */
class CountOfRepeats {

	/**
	 * The current number of remaining repeats.
	 */
	var current = 0
		private set

	/**
	 * The total number of repeats that have been set.
	 */
	private var total = 0

	/**
	 * Sets the total number of repeats and resets the current repeat count.
	 *
	 * @param count The total number of repeats to set.
	 */
	fun set(count: Int) {
		this.current = count
		this.total = count
	}

	/**
	 * Decreases the current repeat count by one.
	 *
	 * This method is called when a repeat is completed.
	 */
	fun decrease() {
		current -= 1
	}

	/**
	 * Clears the current and total repeat counts, resetting them to zero.
	 *
	 * This method is useful when you want to stop all repeats.
	 */
	fun clear() {
		current = 0
		total = 0
	}

	/**
	 * The number of repeats that have been completed so far.
	 *
	 * This is calculated as the total repeats minus the current remaining repeats.
	 */
	val currentRepeat
		get() = total - current
}
