/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

data class CompoundCommandEvent(
	val guild: Guild?,
	val guildName: String,
	val authorTag: String,
	val authorAvatarUrl: String,
	val dataSender: Member?,
	var delay: DefferedEmbed,
	val messageEmbeds: MutableList<MessageEmbed>,
) {
	constructor(event: GuildMessageReceivedEvent) : this(
		guild = event.guild,
		guildName = event.guild.name,
		authorTag = event.author.asTag,
		authorAvatarUrl = event.author.avatarUrl ?: event.author.defaultAvatarUrl,
		dataSender = event.guild.getMember(event.author),
		delay = DefferedEmbed(),
		messageEmbeds = mutableListOf(),
	)

	constructor(event: SlashCommandEvent) : this(
		guild = event.guild,
		guildName = event.guild?.name ?: "unknow",
		authorTag = event.member?.user?.asTag ?: "user",
		authorAvatarUrl = event.member?.user?.avatarUrl ?: event.member?.user?.defaultAvatarUrl ?: "",
		dataSender = event.guild?.getMember(event.member?.user!!),
		delay = DefferedEmbed(),
		messageEmbeds = mutableListOf(),
	)

	fun appendEmbedMessage(messageEmbed: MessageEmbed) {
		if (messageEmbeds.size < 10) {
			messageEmbeds.add(messageEmbed)
		}
	}
}
