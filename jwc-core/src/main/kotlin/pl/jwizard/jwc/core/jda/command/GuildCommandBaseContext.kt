/*
 * Copyright (c) 2025 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import java.math.BigInteger

/**
 * Interface representing the context of a command executed within a guild.
 *
 * @author Miłosz Gilga
 */
interface GuildCommandBaseContext : CommandBaseContext {

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
	 * The member object representing the bot within the guild.
	 */
	val selfMember: Member

	/**
	 * The text channel in which the command was executed. This is the context where the interaction happens.
	 */
	val textChannel: TextChannel
}
