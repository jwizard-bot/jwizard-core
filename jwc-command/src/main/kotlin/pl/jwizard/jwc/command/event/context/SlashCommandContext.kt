/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.context

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import pl.jwizard.jwc.command.GuildCommandProperties
import pl.jwizard.jwc.core.util.ext.avatarOrDefaultUrl

/**
 * Context for handling slash commands within a guild.
 *
 * This class encapsulates the details of the slash command interaction event, along with the guild-specific command
 * properties, to facilitate command processing.
 *
 * @property event The [SlashCommandInteractionEvent] that triggered the command.
 * @property guildCommandProperties The properties specific to the guild where the command is executed.
 * @author Miłosz Gilga
 */
class SlashCommandContext(
	private val event: SlashCommandInteractionEvent,
	private val guildCommandProperties: GuildCommandProperties,
) : CommandContext(guildCommandProperties) {

	override val guildId
		get() = event.guild!!.id

	override val guildName
		get() = event.guild!!.name

	override val authorId
		get() = event.user.id

	override val authorAvatarUrl
		get() = event.user.avatarOrDefaultUrl

	override val authorName
		get() = event.user.name

	override val guildLanguage
		get() = guildCommandProperties.lang

	override val prefix
		get() = "/"
}
