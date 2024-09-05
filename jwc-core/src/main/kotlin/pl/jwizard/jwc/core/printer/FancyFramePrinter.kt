/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

import pl.jwizard.jwc.core.file.ClasspathFileLoader

/**
 * Class extending [AbstractPrinter] with [ConsolePrinterAdapter] as printer adapter responsible for printing fancy
 * frame based elements taken from [fileClasspathLocation] property.
 *
 * @property fileClasspathLocation classpath fancy frame file location
 * @property printerAdapter printer adapter implements [PrinterAdapter] interface
 * @author Miłosz Gilga
 * @see AbstractPrinter
 * @see PrinterAdapter
 */
class FancyFramePrinter(
	private val fileClasspathLocation: String,
	private val printerAdapter: PrinterAdapter
) : AbstractPrinter(printerAdapter) {

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

		val rawFrameElements = loader.readFileRaw() ?: return null
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
