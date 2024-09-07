/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import org.springframework.core.env.PropertySource

/**
 * Interface for loading property sources.
 *
 * @author Miłosz Gilga
 * @see PropertySource
 */
interface PropertySourceLoader {

	/**
	 * Provides the [PropertySource] to be used.
	 *
	 * @return The [PropertySource] instance.
	 */
	fun getSourceLoader(): PropertySource<*>
}