/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.arg

/**
 * Represents the parsed data of a command argument.
 *
 * This class encapsulates the value and type of command argument that has been parsed from a command interaction.
 *
 * @property value The raw string value of the command argument.
 * @property type The type of the command argument, represented by [CommandArgumentType].
 *
 * @author Miłosz Gilga
 */
data class CommandArgumentParsingData(
	val value: String,
	val type: CommandArgumentType,
)
