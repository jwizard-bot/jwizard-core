/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

data class CommandArgDto(
	val name: String,
	val argDesc: Map<String, String?>,
	val type: String,
	val req: Boolean,
	val pos: Long,
	val options: List<CommandArgOptionDto>,
)
