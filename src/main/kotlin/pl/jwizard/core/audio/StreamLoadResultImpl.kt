/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.audio.player.MusicManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.db.RadioStationDto
import pl.jwizard.core.exception.I18nExceptionLocale
import pl.jwizard.core.log.AbstractLoggingBean

class StreamLoadResultImpl(
	private val musicManager: MusicManager,
	private val botConfiguration: BotConfiguration,
	private val event: CompoundCommandEvent,
	private val radioStation: RadioStationDto,
) : AbstractLoggingBean(AudioLoadResultImpl::class), AudioLoadResultHandler {

	override fun trackLoaded(track: AudioTrack?) {
		// star playing radio stream
		track?.let {
			track.userData = event.dataSender
			musicManager.audioPlayer.playTrack(track)
		}
	}

	override fun playlistLoaded(playlist: AudioPlaylist?) {
		playlist?.let { trackLoaded(playlist.tracks[0]) } // refeer to single-source loader
	}

	override fun noMatches() = onError(StringUtils.EMPTY)

	override fun loadFailed(ex: FriendlyException?) = onError(ex?.message)

	private fun onError(cause: String?) {
		val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildErrorMessage(
			placeholder = I18nExceptionLocale.UNXEPECTED_ERROR_ON_LOAD_RADIO,
			params = mapOf("radioStation" to radioStation.name)
		)
		event.instantlySendEmbedMessage(messageEmbed)
		jdaLog.error(event, "Unexpected error on load radio stream ${radioStation.name}. Cause: $cause")
	}
}
