/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

/**
 * Class defining console printer. Based on [Printer] contract.
 *
 * @author Miłosz Gilga
 * @see Printer
 */
class ConsolePrinter : Printer {

	override fun print(content: String) {
		println(content)
	}
}
