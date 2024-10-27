/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import dev.arbjerg.lavalink.client.player.*
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.audio.AudioSender
import pl.jwizard.jwc.audio.loader.spinner.TrackMenuOption
import pl.jwizard.jwc.audio.loader.spinner.TrackSelectSpinnerAction
import pl.jwizard.jwc.audio.loader.spinner.TrackSelectSpinnerMenu
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.duration
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwl.util.logger

/**
 * Handles the loading of tracks into the queue for the music manager.
 *
 * @property musicManager The music manager responsible for managing the guild's audio playback.
 * @author Miłosz Gilga
 */
class QueueTrackLoader(
	private val musicManager: GuildMusicManager,
) : AudioCompletableFutureLoader(musicManager), TrackSelectSpinnerAction {

	companion object {
		private val log = logger<QueueTrackLoader>()
	}

	/**
	 * Properties of the guild that may affect track selection behavior.
	 */
	private val guildProperties = musicManager.beans.environmentBean.getGuildMultipleProperties(
		guildProperties = listOf(
			GuildProperty.RANDOM_AUTO_CHOOSE_TRACK,
			GuildProperty.TIME_AFTER_AUTO_CHOOSE_SEC,
			GuildProperty.MAX_TRACKS_TO_CHOOSE,
		),
		guildId = musicManager.state.context.guild.idLong,
	)

	/**
	 * Handles the loading of a single track.
	 *
	 * @param result The result of the loaded track.
	 * @param future The future response to complete once the track is loaded.
	 */
	override fun onCompletableTrackLoaded(result: TrackLoaded, future: TFutureResponse) {
		val context = musicManager.state.context
		result.track.setUserData(AudioSender(context.author.idLong))

		onEnqueueTrack(result.track)
		log.jdaInfo(context, "Added to queue: %s track by: %s.", result.track.qualifier, context.author.qualifier)

		val response = CommandResponse.Builder()
			.addEmbedMessages(createTrackResponseMessage(result.track))
			.build()
		future.complete(response)
	}

	/**
	 * Handles the loading of search results.
	 *
	 * @param result The search result containing tracks.
	 * @param future The future response to complete with the search results.
	 */
	override fun onCompletableSearchResultLoaded(result: SearchResult, future: TFutureResponse) {
		val response = if (result.tracks.isEmpty()) {
			onError(
				AudioLoadFailedDetails.Builder()
					.setLogMessage("Unable to find any audio track.")
					.setI18nLocaleSource(I18nExceptionSource.NOT_FOUND_TRACK)
					.build()
			)
		} else {
			result.tracks.forEach { it.setUserData(AudioSender(musicManager.state.context.author.idLong)) }
			val options = result.tracks.map { TrackMenuOption(it) }

			val trackSelectSpinnerMenu = TrackSelectSpinnerMenu(musicManager, options, guildProperties, this)

			val (message, components) = trackSelectSpinnerMenu.createMenuComponent(
				i18nBean = musicManager.beans.i18nBean,
				jdaColorStoreBean = musicManager.beans.jdaColorStoreBean,
				i18nSource = I18nResponseSource.SELECT_SONG_SEQUENCER
			)
			CommandResponse.Builder()
				.addEmbedMessages(message)
				.addActionRows(components)
				.disposeComponents(false)
				.onSendAction { trackSelectSpinnerMenu.initEvent(musicManager.beans.eventQueueBean, it) }
				.build()
		}
		future.complete(response)
	}

	/**
	 * Handles the loading of a playlist.
	 *
	 * @param result The result of the loaded playlist.
	 * @param future The future response to complete once the playlist is loaded.
	 */
	override fun onCompletablePlaylistLoaded(result: PlaylistLoaded, future: TFutureResponse) {
		val context = musicManager.state.context
		result.tracks.forEach { it.setUserData(AudioSender(context.author.idLong)) }

		musicManager.state.audioScheduler.loadContent(result.tracks)
		val durationTime = millisToDTF(result.tracks.sumOf { it.duration })

		val messageBuilder = musicManager.createEmbedBuilder()
			.setTitle(I18nAudioSource.ADD_NEW_PLAYLIST)
			.setKeyValueField(I18nAudioSource.COUNT_OF_TRACKS, result.tracks.size)
			.setSpace()
			.setKeyValueField(I18nAudioSource.TRACKS_TOTAL_DURATION_TIME, durationTime)
			.setKeyValueField(I18nAudioSource.TRACK_ADDED_BY, context.author.user.name)

		if (result.tracks.isNotEmpty()) {
			messageBuilder.setArtwork(result.tracks[0].thumbnailUrl)
		}
		val message = messageBuilder
			.setColor(JdaColor.PRIMARY)
			.build()

		log.jdaInfo(context, "Added to queue: %s tracks by: %s.", result.tracks.size, context.author.qualifier)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()
		future.complete(response)
	}

	/**
	 * Handles a failed loading attempt for a track or playlist.
	 *
	 * @param result The result containing the details of the load failure.
	 * @return The details of the load failure.
	 */
	override fun onCompletableLoadFailed(result: LoadFailed) = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unexpected exception during load audio track/playlist. Cause: %s.", result.exception.message)
		.setI18nLocaleSource(I18nExceptionSource.ISSUE_WHILE_LOADING_TRACK)
		.build()

	/**
	 * Handles cases where no tracks or playlists match the search.
	 *
	 * @return The details indicating no matches were found.
	 */
	override fun onCompletableNoMatches() = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unable to find any audio track/playlist.")
		.setI18nLocaleSource(I18nExceptionSource.NOT_FOUND_TRACK)
		.build()

	/**
	 * Enqueues the specified track to the music manager's audio scheduler.
	 *
	 * @param track The track to be added to the queue.
	 */
	override fun onEnqueueTrack(track: Track) {
		musicManager.state.audioScheduler.loadContent(listOf(track))
	}

	/**
	 * Creates a message embed for the specified track that indicates it has been added to the queue.
	 *
	 * @param track The track for which the embed message is created.
	 * @return The created embed message.
	 */
	override fun createTrackResponseMessage(track: Track): MessageEmbed {
		val context = musicManager.state.context
		val queueSize = musicManager.state.queueTrackScheduler.queue.size
		val trackPosition = if (queueSize == 1) {
			musicManager.beans.i18nBean.t(I18nAudioSource.NEXT_TRACK_INDEX_MESS, context.guildLanguage)
		} else {
			queueSize.toString()
		}
		val messageBuilder = musicManager.createEmbedBuilder()
			.setTitle(I18nAudioSource.ADD_NEW_TRACK)
			.setKeyValueField(I18nAudioSource.TRACK_NAME, track.mdTitleLink)
			.setSpace()
			.setKeyValueField(I18nAudioSource.TRACK_DURATION_TIME, millisToDTF(track.duration))

		if (queueSize > 0) {
			messageBuilder.setKeyValueField(I18nAudioSource.TRACK_POSITION_IN_QUEUE, trackPosition)
			messageBuilder.setSpace()
		}
		return messageBuilder
			.setKeyValueField(I18nAudioSource.TRACK_ADDED_BY, context.author.user.name)
			.setArtwork(track.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()
	}

	/**
	 * Handles the error that occurs during the audio loading process.
	 *
	 * @param details The details of the error that occurred.
	 * @return A command response indicating the error.
	 */
	override fun onError(details: AudioLoadFailedDetails): CommandResponse {
		// If there are no tracks in the queue and no current track playing, start the leave waiter.
		if (musicManager.state.queueTrackScheduler.queue.size == 0 && musicManager.cachedPlayer?.track == null) {
			musicManager.startLeavingWaiter()
		}
		return super.onError(details)
	}
}
