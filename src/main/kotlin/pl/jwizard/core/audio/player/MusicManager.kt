/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import pl.jwizard.core.audio.AudioPlayerSendHandler
import pl.jwizard.core.audio.scheduler.AudioScheduler
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.db.GuildDbProperty
import pl.jwizard.core.log.AbstractLoggingBean

class MusicManager(
	private val botConfiguration: BotConfiguration,
	private val event: CompoundCommandEvent,
	audioPlayerManager: AudioPlayerManager,
	lockedGuilds: MutableList<String>,
) : AbstractLoggingBean(MusicManager::class) {

	val audioPlayer: AudioPlayer = audioPlayerManager.createPlayer()
	val audioScheduler = AudioScheduler(botConfiguration, event, audioPlayer, lockedGuilds)
	val audioPlayerSendHandler = AudioPlayerSendHandler(event.guild, audioPlayer)

	init {
		audioPlayer.volume = getVolumeForGuild()
		audioPlayer.addListener(audioScheduler)
	}

	fun resetPlayerVolume(): Int {
		val guildVolume = getVolumeForGuild()
		audioPlayer.volume = guildVolume
		jdaLog.info(event, "Audio player volume was reset to default value ($guildVolume points)")
		return guildVolume
	}

	private fun getVolumeForGuild(): Int = botConfiguration.guildSettingsSupplier.fetchDbProperty(
		GuildDbProperty.DEFAULT_VOLUME,
		event.guildId,
		Int::class
	)

	val actions get() = audioScheduler.schedulerActions

	val queue get() = audioScheduler.schedulerActions.trackQueue

	val currentPlayerVolume get() = audioPlayer.volume
}
