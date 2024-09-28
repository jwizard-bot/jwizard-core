/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.integrity

import org.apache.commons.collections4.CollectionUtils
import kotlin.enums.enumEntries
import kotlin.reflect.KClass

/**
 * Interface for classes that need to ensure referential integrity for the local and remote properties.
 *
 * Implementing this interface allows for defining properties that serve as unique identifiers for various
 * command-related entities.
 *
 * @author Miłosz Gilga
 */
interface ReferentialIntegrityChecker {

	companion object {
		/**
		 * Checks the integrity of a collection of properties against the defined enum entries.
		 *
		 * This function verifies that the provided source collection of strings matches the expected property names
		 * defined in the enum type. If there is any discrepancy, a [DataIntegrityViolationException] is thrown, indicating
		 * that the integrity of the data has been compromised.
		 *
		 * @param T The type of enum implementing ReferentialIntegrityChecker.
		 * @param clazz The class type associated with the integrity check.
		 * @param source The collection of property names to validate.
		 * @throws DataIntegrityViolationException if the integrity check fails.
		 */
		@JvmStatic
		inline fun <reified T> checkIntegrity(
			clazz: KClass<*>,
			source: Collection<String>
		) where T : Enum<T>, T : ReferentialIntegrityChecker {
			val dataEntries = enumEntries<T>()
			if (!CollectionUtils.isEqualCollection(source, dataEntries.map { it.propName })) {
				throw DataIntegrityViolationException(clazz, dataEntries[0].moduleIntegrityName)
			}
		}
	}

	/**
	 * The property name representing the unique identifier for this entity. This is used for matching commands and
	 * arguments within the system.
	 */
	val propName: String

	/**
	 * The name of the module that this entity belongs to, used to enforce integrity constraints and ensure proper
	 * categorization of commands.
	 */
	val moduleIntegrityName: String
}
