/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.arg.CommandArgumentData
import pl.jwizard.core.settings.GuildSettings
import pl.jwizard.core.util.BotUtils
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

data class CompoundCommandEvent(
	val guild: Guild?,
	val guildId: String,
	val guildName: String,
	val authorTag: String,
	val authorAvatarUrl: String,
	val dataSender: Member?,
	val author: User,
	val member: Member,
	var delay: DefferedEmbed,
	val textChannel: TextChannel,
	val commandArgs: MutableMap<CommandArgument, CommandArgumentData>,
	val systemTextChannel: TextChannel,
	val messageEmbeds: MutableList<MessageEmbed>,
	val slashCommandEvent: SlashCommandEvent?,
	var appendAfterEmbeds: (() -> Unit)?,
) {
	val botMember = guild?.selfMember

	constructor(event: GuildMessageReceivedEvent) : this(
		guild = event.guild,
		guildId = event.guild.id,
		guildName = event.guild.name,
		authorTag = event.author.asTag,
		authorAvatarUrl = event.author.avatarUrl ?: event.author.defaultAvatarUrl,
		dataSender = event.guild.getMember(event.author),
		author = event.author,
		member = event.member!!,
		delay = DefferedEmbed(),
		textChannel = event.channel,
		commandArgs = mutableMapOf(),
		systemTextChannel = BotUtils.getSystemTextChannel(event.guild),
		messageEmbeds = mutableListOf(),
		slashCommandEvent = null,
		appendAfterEmbeds = null,
	)

	constructor(event: SlashCommandEvent) : this(
		guild = event.guild,
		guildId = event.guild?.id ?: "",
		guildName = event.guild?.name ?: "unknow",
		authorTag = event.member?.user?.asTag ?: "user",
		authorAvatarUrl = event.member?.user?.avatarUrl ?: event.member?.user?.defaultAvatarUrl ?: "",
		dataSender = event.guild?.getMember(event.member?.user!!),
		author = event.member?.user!!,
		member = event.member!!,
		delay = DefferedEmbed(),
		textChannel = event.textChannel,
		commandArgs = mutableMapOf(),
		systemTextChannel = BotUtils.getSystemTextChannel(event.guild!!),
		messageEmbeds = mutableListOf(),
		slashCommandEvent = event,
		appendAfterEmbeds = null,
	)

	fun appendEmbedMessage(messageEmbed: MessageEmbed) {
		if (messageEmbeds.size < 10) {
			messageEmbeds.add(messageEmbed)
		}
	}

	fun appendEmbedMessage(messageEmbed: MessageEmbed, appendAfterEmbeds: () -> Unit) {
		appendEmbedMessage(messageEmbed)
		this.appendAfterEmbeds = appendAfterEmbeds
	}

	fun instantlySendEmbedMessage(messageEmbed: MessageEmbed, delay: DefferedEmbed) {
		val (duration, unit) = delay
		val deferredMessage = if (slashCommandEvent?.hook?.isExpired == false) {
			slashCommandEvent.hook.sendMessageEmbeds(messageEmbed)
		} else {
			textChannel.sendMessageEmbeds(messageEmbed)
		}
		deferredMessage.queueAfter(duration, unit)
	}

	fun instantlySendEmbedMessage(messageEmbed: MessageEmbed) = instantlySendEmbedMessage(messageEmbed, DefferedEmbed())

	fun checkIfInvokerIsNotSenderOrAdmin(guildSettings: GuildSettings, track: ExtendedAudioTrackInfo): Boolean {
		val trackSender = (track.audioTrack.userData as Member).user
		if (dataSender != null) {
			val validatedUserDetails = BotUtils.validateUserDetails(guildSettings, this)
			return !(trackSender.asTag == author.asTag || validatedUserDetails.concatLogicOr())
		}
		return true
	}
}
