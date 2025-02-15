package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import pl.jwizard.jwc.command.exception.CommandInvocationException
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties

internal class SlashGuildCommandContext(
	override val commandName: String,
	event: SlashCommandInteractionEvent,
	guildCommandProperties: GuildMultipleProperties,
) : GuildCommandContext(guildCommandProperties) {
	override val prefix = "/"

	override val guild = event.guild ?: throw CommandInvocationException("guild is NULL", this)

	override val author = event.member ?: throw CommandInvocationException("author is NULL", this)

	override val textChannel = event.channel.asTextChannel()

	override val selfMember = event.guild?.selfMember
		?: throw CommandInvocationException("bot is NULL", this)

	override val isSlashEvent = true
}
