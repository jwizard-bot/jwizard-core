/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.context

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.core.util.ext.avatarOrDefaultUrl

/**
 * Context for handling commands that use a prefix within a guild.
 *
 * This class provides access to various properties of the message received event, as well as guild-specific command
 * properties.
 *
 * @property event The [MessageReceivedEvent] that triggered the command.
 * @property guildCommandProperties The properties specific to the guild where the command is executed.
 * @author Miłosz Gilga
 */
class PrefixCommandContext(
	private val event: MessageReceivedEvent,
	private val guildCommandProperties: GuildCommandProperties,
) : CommandContext(guildCommandProperties) {

	override val guildId
		get() = event.guild.id

	override val guildName
		get() = event.guild.name

	override val authorId
		get() = event.author.id

	override val authorAvatarUrl
		get() = event.author.avatarOrDefaultUrl

	override val authorName
		get() = event.author.name

	override val guildLanguage
		get() = guildCommandProperties.lang

	override val prefix
		get() = guildCommandProperties.prefix
}
