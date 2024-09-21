/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import pl.jwizard.jwc.command.HashMapAlterKey
import java.math.BigInteger

/**
 * Represents the details of a JDA command.
 *
 * @property id The unique identifier of the command.
 * @property name The name of the command as it will be invoked.
 * @property alias An alternative name or shortcut for the command.
 * @property argI18nKey The internationalization key used for command arguments, if applicable.
 * @property moduleId The identifier of the module that this command belongs to.
 * @property args A mutable list of arguments associated with this command, represented by [CommandArgumentDetails].
 * @author Miłosz Gilga
 */
data class CommandDetails(
	val id: BigInteger,
	val name: String,
	val alias: String,
	val argI18nKey: String?,
	val moduleId: BigInteger,
	val args: MutableList<CommandArgumentDetails>,
) : HashMapAlterKey<String> {

	/**
	 * Retrieves the alias of the command, which serves as a key in hash maps.
	 */
	override val keyAlias
		get() = alias
}
