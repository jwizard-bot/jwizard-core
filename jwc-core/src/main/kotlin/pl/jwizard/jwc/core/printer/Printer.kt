/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

/**
 * Printer contract defining method for printing content by various printers.
 *
 * @author Miłosz Gilga
 */
interface Printer {

	/**
	 * Override this method in class which implements [Printer] interface to print content by various printers
	 * (ex. console or log statement).
	 *
	 * @param content body to print by custom [Printer] printer
	 */
	fun print(content: String)
}
