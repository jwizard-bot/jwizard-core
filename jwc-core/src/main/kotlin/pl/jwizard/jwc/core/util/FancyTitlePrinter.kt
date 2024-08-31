/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import org.slf4j.LoggerFactory
import pl.jwizard.jwc.core.file.ClasspathFileLoader

/**
 * Util class generating and printing fancy title and fancy frames after start the application.
 *
 * @author Miłosz Gilga
 */
class FancyTitlePrinter(
	private val fileClasspathLocation: String,
	private val frameElements: List<String>
) {

	/**
	 * Method responsible for printing fancy title (without frame elements section).
	 *
	 * @author Miłosz Gilga
	 */
	fun printTitle() {
		ClasspathFileLoader(fileClasspathLocation).use {
			if (!it.fileExist()) {
				log.info("Unable to find fancy title file: {}. Printing halted.", fileClasspathLocation)
				return
			}
			it.readFileRaw()?.let { fileContent -> println(fileContent) }
		}
	}

	/**
	 * Method responsible for printing fancy frame elements section. Could be used independently in relation to
	 * [printTitle] method.
	 *
	 * @author Miłosz Gilga
	 */
	fun printFrame() {
		if (frameElements.isEmpty()) {
			log.info("Not declared any frame elements. Printing halted.")
			return
		}
		val maxLengthOfSingleFrameElement = frameElements.maxOf { it.length }
		val horizontalBorder = HORIZONTAL_BORDER.repeat(maxLengthOfSingleFrameElement + 2)

		val lines = mutableListOf<String>()
		lines.add(TOP_EDGE(horizontalBorder))
		frameElements.forEach {
			lines.add("$VERTICAL_BORDER ${it.padEnd(maxLengthOfSingleFrameElement)} $VERTICAL_BORDER")
		}
		lines.add(BOTTOM_EDGE(horizontalBorder))

		val linesWithEOL = lines.joinToString(separator = "\n")
		println(linesWithEOL)
	}

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

		private val log = LoggerFactory.getLogger(FancyTitlePrinter::class.java)
	}
}
