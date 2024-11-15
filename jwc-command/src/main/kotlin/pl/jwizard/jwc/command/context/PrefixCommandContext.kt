/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import pl.jwizard.jwc.command.exception.CommandInvocationException
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty

/**
 * Context for handling commands that use a prefix within a guild.
 *
 * This class provides access to various properties of the message received event, as well as guild-specific command
 * properties.
 *
 * @property event The [MessageReceivedEvent] that triggered the command.
 * @property instancePrefix Instance legacy prefix specified for running multiple instances.
 * @property commandName Definition of the command on which the event was invoked.
 * @property guildCommandProperties The properties specific to the guild where the command is executed.
 * @author Miłosz Gilga
 */
class PrefixCommandContext(
	private val event: MessageReceivedEvent,
	private val instancePrefix: String,
	override val commandName: String,
	private val guildCommandProperties: GuildMultipleProperties,
) : CommandContext(guildCommandProperties) {

	override val prefix = "${guildCommandProperties.getProperty<String>(GuildProperty.LEGACY_PREFIX)}$instancePrefix "
	override val guild = event.guild
	override val author = event.member ?: throw CommandInvocationException("Command is NULL.", this)
	override val textChannel = event.channel.asTextChannel()
	override val selfMember = event.guild.selfMember
	override val isSlashEvent = false
}
