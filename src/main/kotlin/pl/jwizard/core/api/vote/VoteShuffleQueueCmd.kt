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
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.vote.VoteResponseData

@CommandListenerBean(id = BotCommand.VSHUFFLE)
class VoteShuffleQueueCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractVoteMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeVoteMusicCmd(event: CompoundCommandEvent): VoteResponseData {
		val musicManager = playerManagerFacade.findMusicManager(event)
		if (musicManager.queue.isEmpty()) {
			throw AudioPlayerException.TrackQueueIsEmptyException(event)
		}
		val params = mapOf(
			"countOfTracks" to musicManager.queue.size.toString(),
		)
		return VoteResponseData(
			initClazz = VoteShuffleQueueCmd::class,
			message = buildInitMessage(I18nResLocale.VOTE_SUFFLE_QUEUE, params, event),
			onSuccess = {
				playerManagerFacade.shuffleQueue(event)
				buildSuccessMessage(I18nResLocale.SUCCESS_VOTE_SUFFLE_QUEUE, params, it, event)
			},
			onFailure = { buildFailureMessage(I18nResLocale.FAILURE_VOTE_SUFFLE_QUEUE, params, it, event) },
			onTimeout = { buildTimeoutMessage(I18nResLocale.FAILURE_VOTE_SUFFLE_QUEUE, params, it, event) },
		)
	}
}
