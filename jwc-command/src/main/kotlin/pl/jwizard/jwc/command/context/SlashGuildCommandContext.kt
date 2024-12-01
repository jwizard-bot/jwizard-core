/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import pl.jwizard.jwc.command.exception.CommandInvocationException
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties

/**
 * Context for handling slash commands within a guild.
 *
 * This class encapsulates the details of the slash command interaction event, along with the guild-specific command
 * properties, to facilitate command processing.
 *
 * @property event The [SlashCommandInteractionEvent] that triggered the command.
 * @property incomingCommand Definition of the command on which the event was invoked.
 * @property guildCommandProperties The properties specific to the guild where the command is executed.
 * @author Miłosz Gilga
 */
class SlashGuildCommandContext(
	private val event: SlashCommandInteractionEvent,
	private val incomingCommand: String,
	private val guildCommandProperties: GuildMultipleProperties,
) : GuildCommandContext(guildCommandProperties) {

	override val commandName = incomingCommand.replace(".", " ")
	override val prefix = "/"
	override val guild = event.guild ?: throw CommandInvocationException("guild is NULL", this)
	override val author = event.member ?: throw CommandInvocationException("author is NULL", this)
	override val textChannel = event.channel.asTextChannel()
	override val selfMember = event.guild?.selfMember ?: throw CommandInvocationException("bot is NULL", this)
	override val isSlashEvent = true
}
