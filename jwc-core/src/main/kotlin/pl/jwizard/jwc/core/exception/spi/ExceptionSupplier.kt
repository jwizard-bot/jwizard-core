/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.exception.spi

/**
 * Interface for supplying exception-related data.
 *
 * This interface is intended to provide a mechanism for loading exception trackers, which can be used to monitor and
 * manage exceptions within the application.
 *
 * @author Miłosz Gilga
 */
interface ExceptionSupplier {

	/**
	 * Loads a map of exception trackers.
	 *
	 * The map keys represent the types or identifiers of exceptions, while the values represent the counts or other
	 * metrics associated with those exceptions.
	 *
	 * @return A map where keys are exception identifiers and values are their corresponding counts or metrics.
	 */
	fun loadTrackers(): Map<String, Int>
}
