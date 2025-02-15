package pl.jwizard.jwc.command.interaction

import java.util.*

abstract class Component {

	companion object {
		// Separator used to create dynamic component identifier.
		private const val SEPARATOR = "+"
	}

	// Add random group for every component. This ensures that you won't hit a component anywhere in
	// the within a single message with the same ID.
	private val randomGroupId = UUID.randomUUID().toString()

	protected fun createComponentId(id: String) = "$id$SEPARATOR$randomGroupId"

	protected fun getComponentId(
		mergedId: String?,
	) = mergedId?.replace("$SEPARATOR$randomGroupId", "")
}
