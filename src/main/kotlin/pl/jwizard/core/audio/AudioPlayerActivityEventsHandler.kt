/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.settings.GuildSettings
import pl.jwizard.core.util.Formatter
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent

@Component
class AudioPlayerActivityEventsHandler(
	private val playerManagerFacade: PlayerManagerFacade,
	private val guildSettings: GuildSettings,
	private val botConfiguration: BotConfiguration,
) : AbstractLoggingBean(AudioPlayerActivityEventsHandler::class) {

	fun stopPlayingContentAndFreeze(event: GuildVoiceMuteEvent) {
		if (!event.member.user.isBot) {
			return
		}
		val botMember = event.guild.selfMember
		if (botMember.voiceState != null) {
			val musicManager = playerManagerFacade.findMusicManager(event.guild)
			val isMuted = botMember.voiceState!!.isMuted
			val messageEmbed = CustomEmbedBuilder(botConfiguration, guildLang).buildBaseMessage(
				placeholder = if (isMuted) {
					I18nResLocale.PAUSE_TRACK_ON_FORCE_MUTE
				} else {
					I18nResLocale.RESUME_TRACK_ON_FORCE_UNMUTE
				}
			)
			musicManager?.audioPlayer?.isPaused = isMuted
			musicManager?.trackScheduler?.event?.textChannel
				?.sendMessageEmbeds(messageEmbed)
				?.queue()
		}
	}

	fun unsetMusicTextChannelOnDelete(event: TextChannelDeleteEvent) {
		val removedTextChannelId = event.channel.id
		val guildDetails = guildSettings.getGuildProperties(event.guild.id)
		if (removedTextChannelId == guildDetails.musicTextChannelId) {
			guildSettings.removeDefaultMusicTextChannel(event.guild.id)
		}
	}

	fun setBotDeafen(event: GuildVoiceJoinEvent) {
		val guild = event.guild
		if (event.member.id == guild.selfMember.id) {
			guild.audioManager.isSelfDeafened = true
			guild.selfMember.deafen(true).complete()
		}
	}

	fun createDjRoleOnJoin(event: GenericGuildEvent) {
		val guild = event.guild
		val guildDjRoleName = guildSettings.getGuildProperties(guild.id).djRoleName
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
		val guildDjRoleName = guildSettings.getGuildProperties(guild.id).djRoleName
		if (deletedRole.name == guildDjRoleName) {
			generateDjRole(guild, guildDjRoleName)
			log.info("Re-created removed DJ role: {} for guild: {}", guildDjRoleName, Formatter.guildTag(guild))
		}
	}

	private fun generateDjRole(guild: Guild, roleName: String) = guild.createRole()
		.setName(roleName)
		.setColor(EmbedColor.DOMINATE.color())
		.complete()
}
