/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.dj

import pl.jwizard.core.api.AbstractDjCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.entities.MessageEmbed

@CommandListenerBean(id = BotCommand.STOP)
class StopClearQueueCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractDjCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeDjCmd(event: CompoundCommandEvent) {
		val actions = playerManagerFacade.findMusicManager(event).actions
		val currentTrack = playerManagerFacade.currentPlayingTrack(event)

		actions.clearAndDestroy(false)
		actions.leaveAndSendMessageAfterInactivity()

		val messageEmbed: MessageEmbed = if (currentTrack == null) {
			CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
				placeholder = I18nResLocale.CLEAR_QUEUE,
				params = mapOf(
					"countOfTracks" to actions.trackQueue.size,
				),
			)
		} else {
			CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
				placeholder = I18nResLocale.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE,
				params = mapOf(
					"currentTrack" to Formatter.createRichTrackTitle(currentTrack as AudioTrackInfo),
				),
			)
		}
		event.appendEmbedMessage(messageEmbed)
	}
}
