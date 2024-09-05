/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

import pl.jwizard.jwc.core.file.ClasspathFileLoader

/**
 * Class extending [AbstractPrinter] with [ConsolePrinterAdapter] as printer adapter responsible for printing fancy
 * title taken by classpath config file.
 *
 * @constructor Create [FancyTitlePrinter] with defined [fileClasspathLocation] and [printerAdapter].
 * @property fileClasspathLocation classpath fancy title file location
 * @property printerAdapter printer adapter implements [PrinterAdapter] interface
 * @author Miłosz Gilga
 * @see AbstractPrinter
 * @see PrinterAdapter
 */
class FancyTitlePrinter(
	private val fileClasspathLocation: String,
	private val printerAdapter: PrinterAdapter
) : AbstractPrinter(printerAdapter) {

	override fun bodyContent(): String? {
		val loader = ClasspathFileLoader(fileClasspathLocation)
		return loader.readFileRaw()
	}
}
