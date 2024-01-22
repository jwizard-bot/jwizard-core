/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bean

import kotlin.system.exitProcess
import pl.jwizard.core.audio.AloneOnChannelListener
import pl.jwizard.core.audio.PlayerManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.ShutdownEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMuteEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@Component
class BotStatusEventHandler(
	private val _botConfiguration: BotConfiguration,
	private val _aloneOnChannelListener: AloneOnChannelListener,
	private val _playerManager: PlayerManager,
) : ListenerAdapter() {
	private var _shuttingDown = false

	private fun setBotDeafen(event: GuildVoiceJoinEvent) {
		val guild = event.guild
		if (event.member.user.isBot) {
			guild.audioManager.isSelfDeafened = true
			guild.selfMember.deafen(true).complete()
		}
	}

	private fun stopPlayingContentAndFreeze(event: GuildVoiceMuteEvent) {
		if (event.member.user.isBot) {
			val botMember = event.guild.selfMember
			if (botMember.voiceState != null) {
				val musicManager = _playerManager.findMusicManager(event.guild)
				val isMuted = botMember.voiceState!!.isMuted
				

				musicManager?.audioPlayer?.isPaused = isMuted
			}
		}
	}

	private fun shutdownBotInstance(event: ShutdownEvent) {
		if (!_shuttingDown) {
			_shuttingDown = true
			if (event.jda.status != JDA.Status.SHUTTING_DOWN) {
				LOG.info("Shutting down bot instance...")
				for (guild in event.jda.guilds) {
					_playerManager.findMusicManager(guild)?.actions?.clearAndDestroy(false)
					guild.audioManager.closeAudioConnection()
				}
				_botConfiguration.threadPool.shutdownNow()
				event.jda.shutdown()
				LOG.info("Threadpool was cleared and current bot instance was terminated")
				exitProcess(0)
			}
		}
	}

	override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) = setBotDeafen(event)
	override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) = _aloneOnChannelListener.onEveryVoiceUpdate(event)
	override fun onGuildVoiceMute(event: GuildVoiceMuteEvent) = stopPlayingContentAndFreeze(event)
	override fun onShutdown(event: ShutdownEvent) = shutdownBotInstance(event)

	companion object {
		private val LOG = LoggerFactory.getLogger(BotStatusEventHandler::class.java)
	}
}
