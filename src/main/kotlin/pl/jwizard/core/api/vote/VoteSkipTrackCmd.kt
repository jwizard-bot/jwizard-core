/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.vote

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import pl.jwizard.core.api.AbstractVoteMusicCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter
import pl.jwizard.core.vote.VoteResponseData

@CommandListenerBean(id = BotCommand.VSKIP)
class VoteSkipTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractVoteMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
		inPlayingMode = true
	}

	override fun executeVoteMusicCmd(event: CompoundCommandEvent): VoteResponseData {
		val skippingCurrentPlaying = playerManager.currentPlayingTrack(event)
		val params = mapOf(
			"audioTrack" to Formatter.createRichTrackTitle(skippingCurrentPlaying as AudioTrackInfo),
		)
		return VoteResponseData(
			initClazz = VoteSkipTrackCmd::class,
			message = buildInitMessage(I18nResLocale.VOTE_SKIP_TRACK, params, event),
			onSuccess = {
				playerManager.skipTrack(event)
				buildSuccessMessage(I18nResLocale.SUCCESS_VOTE_SKIP_TRACK, params, it, event)
			},
			onFailure = { buildFailureMessage(I18nResLocale.FAILURE_VOTE_SKIP_TRACK, params, it, event) },
			onTimeout = { buildTimeoutMessage(I18nResLocale.FAILURE_VOTE_SKIP_TRACK, params, it, event) },
		)
	}
}
