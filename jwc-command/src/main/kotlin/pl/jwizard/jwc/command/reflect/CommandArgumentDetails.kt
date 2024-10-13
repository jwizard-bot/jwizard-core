/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

/**
 * Represents the details of an argument associated with a command.
 *
 * @property name The name of the argument as it will be used in the command.
 * @property type The data type of the argument (ex. [String], [Integer]).
 * @property required Indicates whether this argument is mandatory for the command execution.
 * @property position The order in which the argument appears in the command.
 * @property options A mutable list of possible values for this argument, if applicable. This is useful for arguments
 *           that have a set of predefined options.
 * @author Miłosz Gilga
 */
data class CommandArgumentDetails(
	val name: String,
	val type: String,
	val required: Boolean,
	val position: Long,
	val options: MutableList<String>,
)
