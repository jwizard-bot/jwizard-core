/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

import pl.jwizard.jwc.core.jda.embed.MessageBaseContext
import java.math.BigInteger

/**
 * Base context interface for commands within a guild.
 *
 * This interface extends [MessageBaseContext] and provides additional context specific to guild commands, such as
 * guild information and command author details.
 *
 * @author Miłosz Gilga
 * @see MessageBaseContext
 */
interface CommandBaseContext : MessageBaseContext {

	/**
	 * The unique identifier of the guild where the command is issued.
	 */
	val guildId: String

	/**
	 * The database identifier of the guild, used for data persistence.
	 */
	val guildDbId: BigInteger

	/**
	 * The name of the guild where the command is executed.
	 */
	val guildName: String

	/**
	 * The unique identifier of the user who issued the command.
	 */
	val authorId: String

	/**
	 * The command prefix used for executing commands in this guild.
	 */
	val prefix: String
}
