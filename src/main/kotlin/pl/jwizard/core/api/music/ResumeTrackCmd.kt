/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo

@CommandListenerBean(id = BotCommand.RESUME)
class ResumeTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
		isPaused = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		playerManagerFacade.resumePausedTrack(event)
		val currentTrack = playerManagerFacade.currentPlayingTrack(event)

		val embedMessage = CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
			placeholder = I18nResLocale.RESUME_TRACK,
			params = mapOf(
				"track" to Formatter.createRichTrackTitle(currentTrack as AudioTrackInfo),
				"invoker" to event.authorTag,
				"pauseCmd" to BotCommand.PAUSE.parseWithPrefix(botConfiguration, event),
			),
		)
		event.appendEmbedMessage(embedMessage)
	}
}
