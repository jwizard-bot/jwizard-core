/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event

/**
 * Data class representing a module's metadata in the command system.
 *
 * @property name The name of the module.
 * @property isActive A boolean indicating whether the module is currently active.
 * @author Miłosz Gilga
 */
data class ModuleData(
	val name: String,
	val isActive: Boolean,
)
