/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

import java.util.*

/**
 * Abstract base class for components used in interaction handling.
 *
 * This class provides common functionality for generating and managing component IDs, ensuring they are unique within
 * a specific context.
 *
 * @author Miłosz Gilga
 */
abstract class Component {

	companion object {
		/**
		 * The separator used to create unique component IDs.
		 */
		private const val SEPARATOR = "+"
	}

	/**
	 * A unique random group ID generated for this component instance. Used to differentiate components of the same type
	 * within the same context.
	 */
	private val randomGroupId = UUID.randomUUID().toString()

	/**
	 * Creates a unique component ID by appending a random group ID to the specified ID.
	 *
	 * @param id The base ID for the component.
	 * @return A unique component ID in the format "id+randomGroupId".
	 */
	protected fun createComponentId(id: String) = "$id$SEPARATOR$randomGroupId"

	/**
	 * Extracts the base component ID from a merged component ID.
	 *
	 * @param mergedId The complete merged ID that includes the random group ID.
	 * @return The base ID, or null if the merged ID is null.
	 */
	protected fun getComponentId(mergedId: String?) = mergedId?.replace("$SEPARATOR$randomGroupId", "")
}
