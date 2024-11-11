/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.math.BigInteger

/**
 * Represents the context of a command execution in the Discord bot framework.
 *
 * This interface encapsulates the necessary information about the command invocation, including details about the
 * guild, the member invoking the command, and the command itself.
 *
 * @author Miłosz Gilga
 */
interface CommandBaseContext {

	/**
	 * Definition of the command on which the event was invoked.
	 */
	val commandName: String

	/**
	 * The command prefix used for executing commands in this guild.
	 */
	val prefix: String

	/**
	 * The language used in the guild. This helps in localizing the content of the embed based on the guild's language
	 * preference.
	 */
	val guildLanguage: String

	/**
	 * The unique database identifier of the guild, which is useful for data persistence and accessing guild-specific
	 * data.
	 */
	val guildDbId: BigInteger

	/**
	 * The guild (server) where the command was executed.
	 */
	val guild: Guild

	/**
	 * The member who invoked the command. This represents the user executing the command.
	 */
	val author: Member

	/**
	 * The member object representing the command author within the guild. This is used to determine the context of the
	 * command's execution.
	 */
	val selfMember: Member

	/**
	 * The text channel in which the command was executed. This is the context where the interaction happens.
	 */
	val textChannel: TextChannel

	/**
	 * Determines if notifications from bot responses should be suppressed.
	 */
	val suppressResponseNotifications: Boolean
}
