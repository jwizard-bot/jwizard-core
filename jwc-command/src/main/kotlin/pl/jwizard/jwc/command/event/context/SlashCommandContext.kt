/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.context

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.event.exception.CommandInvocationException

/**
 * Context for handling slash commands within a guild.
 *
 * This class encapsulates the details of the slash command interaction event, along with the guild-specific command
 * properties, to facilitate command processing.
 *
 * @property event The [SlashCommandInteractionEvent] that triggered the command.
 * @property commandName Definition of the command on which the event was invoked.
 * @property guildCommandProperties The properties specific to the guild where the command is executed.
 * @author Miłosz Gilga
 */
class SlashCommandContext(
	private val event: SlashCommandInteractionEvent,
	override val commandName: String,
	private val guildCommandProperties: GuildCommandProperties,
) : CommandContext(guildCommandProperties) {

	override val prefix = "/"
	override val guild = event.guild ?: throw CommandInvocationException("Guild is NULL.", this)
	override val author = event.member ?: throw CommandInvocationException("Author is NULL.", this)
	override val textChannel = event.channel.asTextChannel()
	override val selfMember = event.guild?.selfMember ?: throw CommandInvocationException("Bot is NULL.", this)
	override val isSlashEvent = true
}
