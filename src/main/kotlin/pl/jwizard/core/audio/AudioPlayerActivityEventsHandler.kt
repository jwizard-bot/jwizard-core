/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import org.springframework.stereotype.Component
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.db.GuildDbProperty
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.Formatter

@Component
class AudioPlayerActivityEventsHandler(
	private val playerManager: PlayerManager,
	private val guildSettingsSupplier: GuildSettingsSupplier,
	private val botConfiguration: BotConfiguration,
) : AbstractLoggingBean(AudioPlayerActivityEventsHandler::class) {

	fun stopPlayingContentAndFreeze(event: GuildVoiceMuteEvent) {
		if (!event.member.user.isBot) {
			return
		}
		val botMember = event.guild.selfMember
		if (botMember.voiceState != null) {
			val musicManager = playerManager.findMusicManager(event.guild.id)
			val guildLang = guildSettingsSupplier.fetchGuildLang(event.guild.id)
			val isMuted = botMember.voiceState!!.isMuted
			val messageEmbed = CustomEmbedBuilder(botConfiguration, guildLang).buildBaseMessage(
				placeholder = if (isMuted) {
					I18nResLocale.PAUSE_TRACK_ON_FORCE_MUTE
				} else {
					I18nResLocale.RESUME_TRACK_ON_FORCE_UNMUTE
				}
			)
			musicManager?.audioPlayer?.isPaused = isMuted
			musicManager?.audioScheduler?.event?.textChannel
				?.sendMessageEmbeds(messageEmbed)
				?.queue()
		}
	}

	fun unsetMusicTextChannelOnDelete(event: ChannelDeleteEvent) {
		if (event.channel.type == ChannelType.TEXT) {
			val removedTextChannelId = event.channel.id
			val musicTextChannelId = guildSettingsSupplier
				.fetchDbProperty(GuildDbProperty.MUSIC_TEXT_CHANNEL_ID, event.guild.id, String::class)
			if (removedTextChannelId == musicTextChannelId) {
				guildSettingsSupplier.removeDefaultMusicTextChannel(event.guild)
			}
		}
	}

	fun setBotDeafen(event: GuildVoiceUpdateEvent) {
		val guild = event.guild
		if (event.channelLeft == null && event.member.id == guild.selfMember.id) {
			guild.audioManager.isSelfDeafened = true
			guild.selfMember.deafen(true).complete()
		}
	}

	fun createDjRoleOnJoin(event: GenericGuildEvent) {
		val guild = event.guild
		val guildDjRoleName = getDjRoleName(guild)
		val djRoles = guild.getRolesByName(guildDjRoleName, false)
		if (djRoles.isEmpty()) {
			val djRole = generateDjRole(guild, guildDjRoleName)
			guild.modifyRolePositions().selectPosition(djRole).moveTo(0).queue()
			log.info("Create and modified DJ role position for guild: {}", Formatter.guildTag(guild))
		}
	}

	fun recreateDjRoleOnDelete(event: RoleDeleteEvent) {
		val deletedRole = event.role
		val guild = event.guild
		val guildDjRoleName = getDjRoleName(guild)
		if (deletedRole.name == guildDjRoleName) {
			generateDjRole(guild, guildDjRoleName)
			log.info("Re-created removed DJ role: {} for guild: {}", guildDjRoleName, Formatter.guildTag(guild))
		}
	}

	private fun generateDjRole(guild: Guild, roleName: String) = guild.createRole()
		.setName(roleName)
		.setColor(EmbedColor.DOMINATE.color())
		.complete()

	private fun getDjRoleName(guild: Guild) = guildSettingsSupplier
		.fetchDbProperty(GuildDbProperty.DJ_ROLE_NAME, guild.id, String::class)
}
