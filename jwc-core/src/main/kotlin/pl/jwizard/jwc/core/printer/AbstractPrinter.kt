/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.printer

import org.slf4j.LoggerFactory

/**
 * Abstract class defining custom content printer (ex. console or log statement printer).
 *
 * @property printerAdapter printer class definition
 * @author Miłosz Gilga
 * @see PrinterAdapter
 */
abstract class AbstractPrinter(private val printerAdapter: PrinterAdapter) {

	companion object {
		private val log = LoggerFactory.getLogger(AbstractPrinter::class.java)
	}

	/**
	 * Method responsible for printing custom content taking from [print] method by declared [printerAdapter]
	 * class.
	 *
	 * @author Miłosz Gilga
	 */
	fun print() {
		bodyContent()
			?.let { printerAdapter.print(it) }
			?: run { log.warn("Unable to find content for {} printing statement.", this::class.simpleName) }
	}

	/**
	 * Method responsible for setting body content printing by [print] method.
	 *
	 * @return body content passing to [print] method; if body content is null, printing is disabled
	 * @author Miłosz Gilga
	 */
	protected abstract fun bodyContent(): String?
}
