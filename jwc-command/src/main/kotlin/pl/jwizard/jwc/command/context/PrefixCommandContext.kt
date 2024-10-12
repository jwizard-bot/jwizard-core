/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.command.exception.CommandInvocationException

/**
 * Context for handling commands that use a prefix within a guild.
 *
 * This class provides access to various properties of the message received event, as well as guild-specific command
 * properties.
 *
 * @property event The [MessageReceivedEvent] that triggered the command.
 * @property commandName Definition of the command on which the event was invoked.
 * @property guildCommandProperties The properties specific to the guild where the command is executed.
 * @author Miłosz Gilga
 */
class PrefixCommandContext(
	private val event: MessageReceivedEvent,
	override val commandName: String,
	private val guildCommandProperties: GuildCommandProperties,
) : CommandContext(guildCommandProperties) {

	override val prefix = guildCommandProperties.prefix
	override val guild = event.guild
	override val author = event.member ?: throw CommandInvocationException("Command is NULL.", this)
	override val textChannel = event.channel.asTextChannel()
	override val selfMember = event.guild.selfMember
	override val isSlashEvent = false
}
