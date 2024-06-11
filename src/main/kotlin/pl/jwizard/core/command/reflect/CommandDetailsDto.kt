/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

data class CommandDetailsDto(
	val id: Long,
	val name: String,
	val alias: String,
	val module: String,
	val commandDesc: Map<String, String?>,
	val argsDesc: Map<String, String?>,
	val args: List<CommandArgDto>,
)