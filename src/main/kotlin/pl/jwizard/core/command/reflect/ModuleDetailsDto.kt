/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

data class ModuleDetailsDto(
	val id: Long,
	val name: Map<String, String?>,
)
