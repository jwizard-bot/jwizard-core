/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import pl.jwizard.core.command.AbstractCompositeCmd

data class CommandProxyData(
	val aliases: List<String>,
	val description: String,
	val category: String,
	var instance: AbstractCompositeCmd?,
) {
	constructor(commandDetailsDto: CommandDetailsDto) : this(
		commandDetailsDto.aliases,
		commandDetailsDto.description,
		commandDetailsDto.category,
		null
	)
}
