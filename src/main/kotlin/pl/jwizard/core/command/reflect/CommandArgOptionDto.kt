/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

data class CommandArgOptionDto(
	val rawValue: String,
	val desc: Map<String, String?>,
	val commandId: Long,
)
