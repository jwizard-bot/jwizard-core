/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import pl.jwizard.core.audio.AudioPlayerSendHandler
import pl.jwizard.core.audio.scheduler.TrackScheduler
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.log.AbstractLoggingBean
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer

class MusicManager(
	private val botConfiguration: BotConfiguration,
	playerManager: AudioPlayerManager,
	private val compoundCommandEvent: CompoundCommandEvent,
	lockedGuilds: MutableList<String>,
) : AbstractLoggingBean(MusicManager::class) {

	val audioPlayer: AudioPlayer = playerManager.createPlayer()
	val trackScheduler = TrackScheduler(botConfiguration, compoundCommandEvent, audioPlayer, lockedGuilds)
	val audioPlayerSendHandler = AudioPlayerSendHandler(compoundCommandEvent.guild, audioPlayer)

	init {
		audioPlayer.volume = getVolumeForGuild()
		audioPlayer.addListener(trackScheduler)
	}

	fun resetPlayerVolume(): Int {
		val guildVolume = getVolumeForGuild()
		audioPlayer.volume = guildVolume
		jdaLog.info(compoundCommandEvent, "Audio player volume was reset to default value ($guildVolume points)")
		return guildVolume
	}

	private fun getVolumeForGuild(): Int {
		val guildDetails = botConfiguration.guildSettings.getGuildProperties(compoundCommandEvent.guildId)
		return guildDetails.audioPlayer.defaultVolume.toInt()
	}

	val actions get() = trackScheduler.schedulerActions

	val queue get() = trackScheduler.schedulerActions.trackQueue

	val currentPlayerVolume get() = audioPlayer.volume
}
