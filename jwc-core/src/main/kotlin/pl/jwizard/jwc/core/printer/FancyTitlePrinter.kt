/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

import pl.jwizard.jwc.core.file.ClasspathFileLoader

/**
 * Class extending [AbstractPrinter] with [ConsolePrinter] as printer responsible for printing fancy
 * title taken by classpath config file.
 *
 * @constructor Create [FancyTitlePrinter] with defined [fileClasspathLocation] and [printer].
 * @property fileClasspathLocation classpath fancy title file location
 * @property printer printer adapter implements [Printer] interface
 * @author Miłosz Gilga
 * @see AbstractPrinter
 * @see Printer
 */
class FancyTitlePrinter(
	private val fileClasspathLocation: String,
	private val printer: Printer
) : AbstractPrinter(printer) {

	override fun bodyContent(): String? {
		val loader = ClasspathFileLoader(fileClasspathLocation)
		return loader.readFileRaw()
	}
}
