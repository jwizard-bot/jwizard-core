/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.vote

import pl.jwizard.core.api.AbstractVoteMusicCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter
import pl.jwizard.core.vote.VoteResponseData

@CommandListenerBean(id = BotCommand.VSKIPTO)
class VoteSkipQueueToTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractVoteMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
		inPlayingMode = true
	}

	override fun executeVoteMusicCmd(event: CompoundCommandEvent): VoteResponseData {
		val musicManager = playerManagerFacade.findMusicManager(event)
		if (musicManager.queue.isEmpty()) {
			throw AudioPlayerException.TrackQueueIsEmptyException(event)
		}
		val trackPosition = getArg<Int>(CommandArgument.POS, event)
		if (musicManager.actions.checkInvertedTrackPosition(trackPosition)) {
			throw AudioPlayerException.TrackPositionOutOfBoundsException(event, musicManager.queue.size)
		}
		val currentPlaying = musicManager.audioPlayer.playingTrack
		val trackToSkipped = musicManager.actions.getTrackByPosition(trackPosition)
		val params = mapOf(
			"audioTrack" to Formatter.createRichTrackTitle(currentPlaying),
			"nextAudioTrack" to Formatter.createRichTrackTitle(trackToSkipped),
			"countOfSkipped" to (trackPosition - 1).toString()
		)
		return VoteResponseData(
			initClazz = VoteSkipQueueToTrackCmd::class,
			message = buildInitMessage(I18nResLocale.VOTE_SKIP_TO_TRACK, params, event),
			onSuccess = {
				playerManagerFacade.skipToTrackPos(event, trackPosition)
				buildSuccessMessage(I18nResLocale.SUCCESS_VOTE_SKIP_TO_TRACK, params, it, event)
			},
			onFailure = { buildFailureMessage(I18nResLocale.FAILURE_VOTE_SKIP_TO_TRACK, params, it, event) },
			onTimeout = { buildTimeoutMessage(I18nResLocale.FAILURE_VOTE_SKIP_TO_TRACK, params, it, event) },
		)
	}
}
