/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.vote

import pl.jwizard.core.api.AbstractVoteMusicCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.vote.VoteResponseData

@CommandListenerBean(id = BotCommand.VSTOP)
class VoteStopAndClearQueueCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractVoteMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeVoteMusicCmd(event: CompoundCommandEvent): VoteResponseData {
		val actions = playerManager.findMusicManager(event).actions
		return VoteResponseData(
			initClazz = VoteStopAndClearQueueCmd::class,
			message = buildInitMessage(I18nResLocale.VOTE_STOP_CLEAR_QUEUE, event),
			onSuccess = {
				actions.clearAndDestroy(false)
				actions.leaveAndSendMessageAfterInactivity()
				buildSuccessMessage(I18nResLocale.SUCCESS_VOTE_STOP_CLEAR_QUEUE, it, event)
			},
			onFailure = { buildFailureMessage(I18nResLocale.FAILURE_VOTE_STOP_CLEAR_QUEUE, it, event) },
			onTimeout = { buildTimeoutMessage(I18nResLocale.FAILURE_VOTE_STOP_CLEAR_QUEUE, it, event) },
		)
	}
}
