/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.context

import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.event.arg.CommandArgumentParsingData
import pl.jwizard.jwc.command.event.exception.CommandParserException
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import java.math.BigInteger

/**
 * Abstract class representing the context of a command execution within a guild. It provides access to command
 * arguments and guild-specific properties needed for command processing.
 *
 * @property guildCommandProperties Properties related to the guild where the command is executed.
 * @author Miłosz Gilga
 */
abstract class CommandContext(private val guildCommandProperties: GuildCommandProperties) : CommandBaseContext {

	/**
	 * A mutable map that stores the parsed command arguments, with their corresponding [CommandArgument] as keys and
	 * their parsed data as values.
	 */
	val commandArguments: MutableMap<CommandArgument, CommandArgumentParsingData> = mutableMapOf()

	/**
	 * The unique database identifier for the guild.
	 */
	override val guildDbId: BigInteger
		get() = guildCommandProperties.guildDbId

	/**
	 * Retrieves and casts the argument value for the specified [CommandArgument].
	 *
	 * @param argument The [CommandArgument] whose value is to be retrieved.
	 * @param T The type to cast the argument value to.
	 * @return The cast value of the argument.
	 * @throws CommandParserException If the argument is not found or cannot be cast to the desired type.
	 */
	inline fun <reified T : Any> getArg(argument: CommandArgument) = try {
		val (value, type) = commandArguments[argument] ?: throw NumberFormatException()
		type.castTo(value) as T
	} catch (ex: NumberFormatException) {
		throw CommandParserException()
	}
}
