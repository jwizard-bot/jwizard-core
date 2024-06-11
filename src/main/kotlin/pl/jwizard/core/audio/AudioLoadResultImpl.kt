/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.core.audio.player.MusicManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.exception.I18nExceptionLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter
import pl.jwizard.core.vote.SongChooserVotingSystemHandler

class AudioLoadResultImpl(
	private val musicManager: MusicManager,
	private val botConfiguration: BotConfiguration,
	private val event: CompoundCommandEvent,
	private val isUrlPattern: Boolean,
	private val lockedGuilds: MutableList<String>
) : AbstractLoggingBean(AudioLoadResultImpl::class), AudioLoadResultHandler {

	override fun trackLoaded(track: AudioTrack?) {
		if (track != null && event.dataSender != null) {
			addNewAudioTrackToQueue(event.dataSender, track)
		}
	}

	override fun playlistLoaded(playlist: AudioPlaylist?) {
		val trackList = playlist?.tracks ?: return
		val sender = event.dataSender ?: return

		if (isUrlPattern) {
			for (audioTrack in trackList) {
				audioTrack.userData = event.dataSender
				musicManager.actions.addToQueueAndOffer(audioTrack)
			}
			val flattedTracks = trackList.joinToString(", ") { it.info.title }
			val messageEmbed = createPlaylistTracksMessage(trackList)
			event.instantlySendEmbedMessage(messageEmbed)

			jdaLog.info(event, "New audio playlist: $flattedTracks was added to queue")
		} else {
			val songChooserVotingSystemHandler = SongChooserVotingSystemHandler(
				trackList,
				botConfiguration,
				event,
				onSelectTrackCallback = { track -> addNewAudioTrackToQueue(sender, track) },
				lockedGuilds
			)
			songChooserVotingSystemHandler.initAndStart()
		}
	}

	override fun noMatches() {
		onError(I18nExceptionLocale.NOT_FOUND_TRACK, "Not available to find provided audio track/playlist")
	}

	override fun loadFailed(ex: FriendlyException?) {
		onError(
			I18nExceptionLocale.ISSUE_WHILE_LOADING_TRACK,
			"Unexpected exception during load audio track/playlist. Cause ${ex?.message}"
		)
	}

	private fun onError(placeholder: I18nExceptionLocale, log: String) {
		val messageEmbed = CustomEmbedBuilder(botConfiguration, event).buildErrorMessage(placeholder)
		val actions = musicManager.trackScheduler.schedulerActions
		if (musicManager.queue.isEmpty() && musicManager.audioPlayer.playingTrack == null) {
			actions.leaveAndSendMessageAfterInactivity()
		}
		event.instantlySendEmbedMessage(messageEmbed)
		jdaLog.error(event, log)

	}

	private fun addNewAudioTrackToQueue(member: Member, track: AudioTrack) {
		track.userData = member
		musicManager.actions.addToQueueAndOffer(track)
		if (!musicManager.queue.isEmpty()) {
			val trackPosition = if (musicManager.queue.size == 1) {
				botConfiguration.i18nService.getMessage(I18nMiscLocale.NEXT_TRACK_INDEX_MESS, event.lang)
			} else {
				musicManager.queue.size.toString()
			}
			jdaLog.info(event, "New audio track: ${track.info.title} was added to queue")
			val messageEmbed = createSingleTrackMessage(track, trackPosition)
			event.instantlySendEmbedMessage(messageEmbed)
		}
	}

	private fun createSingleTrackMessage(track: AudioTrack, trackPosition: String): MessageEmbed {
		val trackInfo = ExtendedAudioTrackInfo(track)
		val addedByMessage = botConfiguration.i18nService.getMessage(I18nMiscLocale.TRACK_ADDDED_BY, event.lang)
		return CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(I18nResLocale.ADD_NEW_TRACK)
			.appendKeyValueField(I18nMiscLocale.TRACK_NAME, Formatter.createRichTrackTitle(trackInfo))
			.addSpace()
			.appendKeyValueField(I18nMiscLocale.TRACK_DURATION_TIME, DateUtils.convertMilisToDTF(track.duration))
			.appendKeyValueField(I18nMiscLocale.TRACK_POSITION_IN_QUEUE, trackPosition)
			.addSpace()
			.appendField("${addedByMessage}:", event.authorTag, true)
			.addThumbnail(trackInfo.artworkUrl)
			.addColor(EmbedColor.WHITE)
			.build()
	}

	private fun createPlaylistTracksMessage(audioTracks: List<AudioTrack>): MessageEmbed {
		val trackInfo = ExtendedAudioTrackInfo(audioTracks[0])
		val durationTime = DateUtils.convertMilisToDTF(audioTracks.sumOf { it.duration })
		return CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(I18nResLocale.ADD_NEW_PLAYLIST)
			.appendKeyValueField(I18nMiscLocale.COUNT_OF_TRACKS, audioTracks.size)
			.addSpace()
			.appendKeyValueField(I18nMiscLocale.TRACKS_TOTAL_DURATION_TIME, durationTime)
			.appendKeyValueField(I18nMiscLocale.TRACK_ADDDED_BY, event.authorTag)
			.addThumbnail(trackInfo.artworkUrl)
			.addColor(EmbedColor.WHITE)
			.build()
	}
}
