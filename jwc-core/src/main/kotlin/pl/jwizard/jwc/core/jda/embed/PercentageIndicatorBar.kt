/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.embed

/**
 * A class that represents a progress indicator bar, showing the percentage of completion between a start and total
 * duration.
 *
 * This class generates a visual representation of progress using blocks to indicate the completion percentage,
 * allowing for easy monitoring of tasks or processes.
 *
 * @property start The current duration that has elapsed.
 * @property total The total duration that needs to be completed.
 * @property length The length of the indicator bar in characters.
 * @author Miłosz Gilga
 */
class PercentageIndicatorBar(
	private val start: Long,
	private val total: Long,
	private val length: Int = MAX_INDICATOR_LENGTH,
) {

	companion object {
		/**
		 * Character representing a filled block in the progress indicator.
		 */
		private const val INDICATOR_FULL = '█'

		/**
		 * Character representing an empty block in the progress indicator.
		 */
		private const val INDICATOR_EMPTY = '▒'

		/**
		 * Maximum length of the indicator bar.
		 */
		private const val MAX_INDICATOR_LENGTH = 36
	}

	/**
	 * Formats a specified number of blocks with a given character. This function repeats the character for a specified
	 * count, creating a string representation of filled or empty blocks.
	 *
	 * @return A string consisting of the character repeated [count] times.
	 */
	private val formatToBlocks: (Int, Char) -> String = { count, character ->
		character.toString().repeat(0.coerceAtLeast(count))
	}

	/**
	 * Generates a string representation of the progress indicator bar.
	 *
	 * This method calculates the completion percentage based on the [start] and [total] durations and creates a visual
	 * representation of the progress using filled and empty blocks.
	 *
	 * @return A string representing the progress indicator bar, showing the current progress.
	 */
	fun generateBar(): String {
		val progressPercentage = start.toDouble() / total.toDouble() * 100.0

		val blocksCount = Math.round(length * progressPercentage / 100).toInt()
		val emptyBlocksCount = length - blocksCount

		return formatToBlocks(blocksCount, INDICATOR_FULL) + formatToBlocks(
			emptyBlocksCount,
			INDICATOR_EMPTY
		) + " %.2f%%".format(progressPercentage)
	}
}
