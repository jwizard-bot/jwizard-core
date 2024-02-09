/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter
import net.dv8tion.jda.api.entities.MessageEmbed

@CommandListenerBean(id = BotCommand.QUEUE)
class ShowQueueCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val musicManager = playerManagerFacade.findMusicManager(event)
		if (musicManager.queue.isEmpty()) {
			throw AudioPlayerException.TrackQueueIsEmptyException(event)
		}
		val pageableTracks = musicManager.queue
			.mapIndexed { index, track -> Formatter.createRichPageableTrackInfo(index, track) }

		val currentTrack = playerManagerFacade.findMusicManager(event).audioPlayer.playingTrack
		var leftToNextTrack = "-"
		if (currentTrack != null) {
			leftToNextTrack = DateUtils.convertMilisToDTF(ExtendedAudioTrackInfo(currentTrack).approximateTime)
		}
		val tracksPaginator = createDefaultPaginator(pageableTracks)
		val messageEmbed = createQueueMessage(
			event,
			queueSize = musicManager.queue.size,
			queueMaxDuration = musicManager.queue.sumOf { it.duration },
			timeToNextTrack = leftToNextTrack,
			averageTrackDuration = musicManager.actions.getAverageTracksDuration(),
			repeatingState = musicManager.actions.infiniteRepeating,
		)
		event.appendEmbedMessage(messageEmbed) { tracksPaginator.display(event.textChannel) }
	}

	private fun createQueueMessage(
		event: CompoundCommandEvent,
		queueSize: Int,
		queueMaxDuration: Long,
		timeToNextTrack: String,
		averageTrackDuration: Long,
		repeatingState: Boolean,
	): MessageEmbed = CustomEmbedBuilder(event, botConfiguration)
		.appendKeyValueField(I18nMiscLocale.ALL_TRACKS_IN_QUEUE_COUNT, queueSize)
		.addSpace()
		.appendKeyValueField(
			I18nMiscLocale.ALL_TRACKS_IN_QUEUE_DURATION,
			DateUtils.convertMilisToDTF(queueMaxDuration)
		)
		.appendKeyValueField(I18nMiscLocale.APPROX_TO_NEXT_TRACK_FROM_QUEUE, timeToNextTrack)
		.addSpace()
		.appendKeyValueField(
			I18nMiscLocale.PLAYLIST_AVERAGE_TRACK_DURATION,
			DateUtils.convertMilisToDTF(averageTrackDuration)
		)
		.appendKeyValueField(
			I18nMiscLocale.PLAYLIST_REPEATING_MODE,
			i18nService.getMessage(Formatter.toStateTag(repeatingState), event.guildId)
		)
		.addColor(EmbedColor.WHITE)
		.build()
}
