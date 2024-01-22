/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import com.fasterxml.jackson.annotation.JsonCreator

data class CommandsResDto(
	val categories: Map<String, String>,
	val commmands: Map<String, CommandDetailsDto>,
) {
	@JsonCreator
	constructor() : this(emptyMap(), emptyMap())
}

data class CommandDetailsDto(
	val aliases: List<String>,
	val category: String,
	val description: String,
) {
	@JsonCreator
	constructor() : this(emptyList(), "", "")
}
