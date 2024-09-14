/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

/**
 * Interface representing a property with a unique key.
 *
 * @author Miłosz Gilga
 */
interface Property {

	/**
	 * The key associated with this property. This key uniquely identifies the property and is used to reference
	 * its value.
	 */
	val key: String
}
