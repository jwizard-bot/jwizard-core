/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import pl.jwizard.core.config.annotation.NoArgConstructor

@NoArgConstructor
data class CommandsResDto(
	val categories: Map<String, String>,
	val commmands: Map<String, CommandDetailsDto>,
	val modules: Map<String, String>,
)

@NoArgConstructor
data class CommandDetailsDto(
	val aliases: List<String>,
	val category: String,
	val desc: String,
	val argsDesc: String?,
	val args: List<CommandArgumentDto>
)

@NoArgConstructor
data class CommandArgumentDto(
	val id: String,
	val name: String,
	val type: String,
	val req: Boolean,
)
