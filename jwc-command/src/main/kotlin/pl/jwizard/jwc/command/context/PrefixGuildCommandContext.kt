package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import pl.jwizard.jwc.command.exception.CommandInvocationException
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty

internal class PrefixGuildCommandContext(
	override val commandName: String,
	event: MessageReceivedEvent,
	instancePrefix: String,
	guildCommandProperties: GuildMultipleProperties,
) : GuildCommandContext(guildCommandProperties) {
	override val prefix =
		"${guildCommandProperties.getProperty<String>(GuildProperty.LEGACY_PREFIX)}$instancePrefix "

	override val guild = event.guild

	override val author = event.member ?: throw CommandInvocationException("author is NULL", this)

	override val textChannel = event.channel.asTextChannel()

	override val selfMember = event.guild.selfMember

	override val isSlashEvent = false
}
