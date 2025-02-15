package pl.jwizard.jwc.core.jda.embed

class PercentageIndicatorBar(
	private val start: Long,
	private val total: Long,
	private val length: Int = MAX_INDICATOR_LENGTH,
) {

	companion object {
		private const val INDICATOR_FULL = '█'
		private const val INDICATOR_EMPTY = '▒'
		private const val MAX_INDICATOR_LENGTH = 36
	}

	private val formatToBlocks: (Int, Char) -> String = { count, character ->
		character.toString().repeat(0.coerceAtLeast(count))
	}

	fun generateBar(showPercentageNumber: Boolean = false): String {
		val progressPercentage = start.toDouble() / total.toDouble() * 100.0

		val blocksCount = Math.round(length * progressPercentage / 100).toInt()
		val emptyBlocksCount = length - blocksCount

		var blocksContent = formatToBlocks(blocksCount, INDICATOR_FULL) + formatToBlocks(
			emptyBlocksCount,
			INDICATOR_EMPTY
		)
		if (showPercentageNumber) {
			blocksContent += " %.2f%%".format(progressPercentage)
		}
		return blocksContent
	}
}
