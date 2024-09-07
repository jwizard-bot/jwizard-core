/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

import pl.jwizard.jwc.core.file.ClasspathFileLoader

/**
 * Class extending [AbstractPrinter] with [ConsolePrinter] as printer responsible for printing fancy
 * frame based elements taken from [fileClasspathLocation] property.
 *
 * @constructor Create [FancyFramePrinter] with defined [fileClasspathLocation] and [printer].
 * @property fileClasspathLocation classpath fancy frame file location
 * @property printer printer adapter implements [Printer] interface
 * @author Miłosz Gilga
 * @see AbstractPrinter
 * @see Printer
 */
class FancyFramePrinter(
	private val fileClasspathLocation: String,
	private val printer: Printer
) : AbstractPrinter(printer) {

	companion object {
		/**
		 * Fancy frame horizontal border character.
		 */
		private const val HORIZONTAL_BORDER = "─"

		/**
		 * Fancy frame vertical border character.
		 */
		private const val VERTICAL_BORDER = "│"

		/**
		 * Fancy frame top. Wrap passed string as argument with fancy corners.
		 */
		private val TOP_EDGE: (String) -> String = { "╭$it╮" }

		/**
		 * Fancy frame bottom. Wrap passed string as argument with fancy corners.
		 */
		private val BOTTOM_EDGE: (String) -> String = { "╰$it╯" }
	}

	override fun bodyContent(): String? {
		val loader = ClasspathFileLoader(fileClasspathLocation)

		val rawFrameElements = loader.readFileRaw()
		val frameElements = rawFrameElements
			.split(regex = "\\r?\\n|\\r".toRegex())
			.dropLastWhile { it.isEmpty() }
			.toTypedArray()

		if (frameElements.isEmpty()) {
			return null
		}
		val maxLengthOfSingleFrameElement = frameElements.maxOf { it.length }
		val horizontalBorder = HORIZONTAL_BORDER.repeat(maxLengthOfSingleFrameElement + 2)

		val lines = mutableListOf<String>()
		lines.add(TOP_EDGE(horizontalBorder))
		frameElements.forEach {
			lines.add("$VERTICAL_BORDER ${it.padEnd(maxLengthOfSingleFrameElement)} $VERTICAL_BORDER")
		}
		lines.add(BOTTOM_EDGE(horizontalBorder))

		return lines.joinToString(separator = "\n")
	}
}
