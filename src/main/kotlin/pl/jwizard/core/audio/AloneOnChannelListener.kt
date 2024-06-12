/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.springframework.stereotype.Component
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.db.GuildDbProperty
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.Formatter
import java.time.Instant
import java.util.concurrent.TimeUnit

@Component
class AloneOnChannelListener(
	private val botConfiguration: BotConfiguration,
	private val playerManagerFacade: PlayerManagerFacade,
	private val guildSettingsSupplier: GuildSettingsSupplier,
) : AbstractLoggingBean(AloneOnChannelListener::class) {

	private final lateinit var jda: JDA
	private final val aloneFromTime = mutableMapOf<Long, Instant>()

	fun initialize(jda: JDA) {
		this.jda = jda
		botConfiguration.threadPool.scheduleWithFixedDelay({ concurrentAloneCheck() }, 0, 5, TimeUnit.SECONDS)
	}

	fun onEveryVoiceUpdate(event: GuildVoiceUpdateEvent) {
		val guild = event.guild
		if (guild.audioManager.sendingHandler != null) {
			val isAlone = isAloneOnChannel(guild)
			val isAlonePrevious = aloneFromTime.containsKey(guild.idLong)
			if (!isAlone && isAlonePrevious) {
				aloneFromTime.remove(guild.idLong)
			} else if (isAlone && !isAlonePrevious) {
				aloneFromTime[guild.idLong] = Instant.now()
			}
		}
	}

	private fun concurrentAloneCheck() {
		val removeFromGuild = mutableSetOf<Long>()
		for ((guildId, time) in aloneFromTime) {
			val guild = jda.getGuildById(guildId)
			if (guild == null) {
				removeFromGuild.add(guildId)
				continue
			}
			val maxInactivity = guildSettingsSupplier
				.fetchDbProperty(GuildDbProperty.LEAVE_EMPTY_CHANNEL_SEC, guild.id, Int::class)
			if (time.epochSecond > (Instant.now().epochSecond - maxInactivity)) {
				continue
			}
			val musicManager = playerManagerFacade.findMusicManager(guild)
			musicManager?.actions?.clearAndDestroy(true)
			guild.audioManager.closeAudioConnection()

			log.info("Leave voice channel in guild: {}. Cause: not found any active user", Formatter.guildTag(guild))
			removeFromGuild.add(guildId)
		}
		removeFromGuild.forEach { aloneFromTime.remove(it) }
	}

	private fun isAloneOnChannel(guild: Guild): Boolean {
		val connectedChannel = guild.audioManager.connectedChannel ?: return false
		return connectedChannel.members.none { it.voiceState?.isDeafened == false && !it.user.isBot }
	}
}
