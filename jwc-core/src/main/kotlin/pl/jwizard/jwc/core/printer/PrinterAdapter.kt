/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

/**
 * Printer adapter contract defining method for printing content by various printers.
 *
 * @author Miłosz Gilga
 */
interface PrinterAdapter {

	/**
	 * Override this method in class which implements [PrinterAdapter] interface to print content by various printers
	 * (ex. console or log statement).
	 *
	 * @param content body to print by custom [PrinterAdapter] printer
	 */
	fun print(content: String)
}
