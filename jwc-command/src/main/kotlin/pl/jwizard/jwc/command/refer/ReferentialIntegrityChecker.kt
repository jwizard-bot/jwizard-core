/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.refer

/**
 * Interface for classes that need to ensure referential integrity within the application's command structure.
 *
 * Implementing this interface allows for defining properties that serve as unique identifiers for various
 * command-related entities.
 *
 * @author Miłosz Gilga
 */
interface ReferentialIntegrityChecker {

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
