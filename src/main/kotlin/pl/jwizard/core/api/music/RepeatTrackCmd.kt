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
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo

@CommandListenerBean(id = BotCommand.REPEAT)
class RepeatTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		inPlayingMode = true
		onSameChannelWithBot = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val repeatsCount = getArg<Int>(CommandArgument.REPEATS, event)

		val guildDetails = guildSettings.getGuildProperties(event.guildId)
		val maxRepeats = guildDetails.audioPlayer.maxRepeatsOfTrack
		if (repeatsCount < 2 || repeatsCount > maxRepeats) {
			throw AudioPlayerException.TrackRepeatsOutOfBoundsException(event, maxRepeats.toInt())
		}
		playerManagerFacade.setTrackRepeat(event, repeatsCount)

		val currentPlayingTrack = playerManagerFacade.currentPlayingTrack(event)
		val embedMessage = CustomEmbedBuilder(event, botConfiguration).buildBaseMessage(
			placeholder = I18nResLocale.SET_MULTIPLE_REPEATING_TRACK,
			params = mapOf(
				"track" to Formatter.createRichTrackTitle(currentPlayingTrack as AudioTrackInfo),
				"times" to repeatsCount,
				"clearRepeatingCmd" to BotCommand.REPEATCLS.parseWithPrefix(botConfiguration, event),
			),
		)
		event.appendEmbedMessage(embedMessage)
	}
}
