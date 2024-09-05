/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

/**
 * Class defining console printer adapter. Based on [PrinterAdapter] contract.
 *
 * @author Miłosz Gilga
 * @see PrinterAdapter
 */
class ConsolePrinterAdapter : PrinterAdapter {

	override fun print(content: String) {
		println(content)
	}
}
