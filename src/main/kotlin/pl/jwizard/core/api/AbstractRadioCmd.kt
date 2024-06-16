/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.audio.AudioPlayerSendHandler
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.RadioException

abstract class AbstractRadioCmd(
	protected val playerManager: PlayerManager,
	botConfiguration: BotConfiguration
) : AbstractAudioCmd(
	botConfiguration
) {
	protected var isRadioShouldPlaying = false // available only if radio is currently playing
	protected var isRadioShouldIdle = false // available only if radio is currently in idle mode (not playing)

	override fun executeAudioCmd(sendHandler: AudioPlayerSendHandler?, event: CompoundCommandEvent) {
		checkIfCommandModuleIsEnabled(event, CommandModule.RADIO_STATION)

		val userVoiceState = validateUserVoiceState(event)
		val openAudioConnection = checkIfUserIsWithBotOnAudioChannel(userVoiceState, event)

		val musicManager = playerManager.findMusicManager(event)

		// check, if non-continuous audio source is active (track)
		if (!musicManager.audioScheduler.isStreamFacade() && musicManager.audioPlayer.playingTrack != null) {
			throw RadioException.DiscreteAudioStreamIsPlayingException(event)
		}
		// check, if radio is currently active playing
		if (isRadioShouldPlaying) {
			if (musicManager.actions.radioStationDto == null || musicManager.audioPlayer.playingTrack == null) {
				throw RadioException.RadioStationIsNotPlayingException(event)
			}
		} else if (isRadioShouldIdle) { // or else check if radio is playing and should be in idle mode
			if (musicManager.actions.radioStationDto != null && musicManager.audioPlayer.playingTrack != null) {
				throw RadioException.RadioStationIsPlayingException(event)
			}
		}
		executeRadioCmd(event, openAudioConnection)
	}

	protected abstract fun executeRadioCmd(event: CompoundCommandEvent, openAudioConnection: Boolean)
}
