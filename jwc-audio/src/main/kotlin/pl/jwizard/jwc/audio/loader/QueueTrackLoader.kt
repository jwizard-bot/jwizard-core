/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwac.event.onload.*
import pl.jwizard.jwac.player.track.AudioSender
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.audio.loader.spinner.TrackMenuOption
import pl.jwizard.jwc.audio.loader.spinner.TrackSelectSpinnerAction
import pl.jwizard.jwc.audio.loader.spinner.TrackSelectSpinnerMenu
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.util.logger

/**
 * Handles the loading of tracks into the queue for the music manager.
 *
 * @property guildMusicManager The music manager responsible for managing the guild's audio playback.
 * @author Miłosz Gilga
 */
class QueueTrackLoader(
	private val guildMusicManager: GuildMusicManager,
) : AudioCompletableFutureLoader(guildMusicManager), TrackSelectSpinnerAction {

	companion object {
		private val log = logger<QueueTrackLoader>()
	}

	/**
	 * Properties of the guild that may affect track selection behavior.
	 */
	private val guildProperties = guildMusicManager.bean.environment.getGuildMultipleProperties(
		guildProperties = listOf(
			GuildProperty.RANDOM_AUTO_CHOOSE_TRACK,
			GuildProperty.TIME_AFTER_AUTO_CHOOSE_SEC,
			GuildProperty.MAX_TRACKS_TO_CHOOSE,
		),
		guildId = guildMusicManager.state.context.guild.idLong,
	)

	/**
	 * Handles the loading of a single track.
	 *
	 * @param result The result of the loaded track.
	 * @param future The future response to complete once the track is loaded.
	 */
	override fun onCompletableTrackLoaded(result: KTrackLoadedEvent, future: TFutureResponse) {
		val context = guildMusicManager.state.context
		result.track.setSenderData(AudioSender(context.author.idLong))

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
	override fun onCompletableSearchResultLoaded(result: KSearchResultEvent, future: TFutureResponse) {
		if (result.tracks.isEmpty()) {
			onError(
				AudioLoadFailedDetails.Builder()
					.setLogMessage("Unable to find any audio track.")
					.setI18nLocaleSource(I18nExceptionSource.NOT_FOUND_TRACK)
					.build()
			)
			return
		}
		result.tracks.forEach { it.setSenderData(AudioSender(guildMusicManager.state.context.author.idLong)) }
		val options = result.tracks.map { TrackMenuOption(it) }

		val trackSelectSpinnerMenu = TrackSelectSpinnerMenu(guildMusicManager, options, guildProperties, this)

		val (message, components) = trackSelectSpinnerMenu.createMenuComponent(
			i18nBean = guildMusicManager.bean.i18n,
			jdaColorsCache = guildMusicManager.bean.jdaColorStore,
			i18nSource = I18nResponseSource.SELECT_SONG_SEQUENCER
		)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(components)
			.disposeComponents(false)
			.onSendAction { trackSelectSpinnerMenu.initEvent(guildMusicManager.bean.eventQueue, it) }
			.build()
		future.complete(response)
	}

	/**
	 * Handles the loading of a playlist.
	 *
	 * @param result The result of the loaded playlist.
	 * @param future The future response to complete once the playlist is loaded.
	 */
	override fun onCompletablePlaylistLoaded(result: KPlaylistLoadedEvent, future: TFutureResponse) {
		val context = guildMusicManager.state.context
		result.tracks.forEach { it.setSenderData(AudioSender(context.author.idLong)) }

		guildMusicManager.state.audioScheduler.loadContent(result.tracks)
		val durationTime = millisToDTF(result.tracks.sumOf { it.duration })

		val messageBuilder = guildMusicManager.createEmbedBuilder()
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
	override fun onCompletableLoadFailed(result: KLoadFailedEvent) = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unexpected exception during load audio track/playlist. Cause: %s.", result.exception.message)
		.setI18nLocaleSource(I18nExceptionSource.ISSUE_WHILE_LOADING_TRACK)
		.build()

	/**
	 * Handles cases where no tracks or playlists match the search.
	 *
	 * @param result The no matches result.
	 * @return The details indicating no matches were found.
	 */
	override fun onCompletableNoMatches(result: KNoMatchesEvent) = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unable to find any audio track/playlist.")
		.setI18nLocaleSource(I18nExceptionSource.NOT_FOUND_TRACK)
		.build()

	/**
	 * Enqueues the specified track to the music manager's audio scheduler.
	 *
	 * @param track The track to be added to the queue.
	 */
	override fun onEnqueueTrack(track: Track) {
		guildMusicManager.state.audioScheduler.loadContent(listOf(track))
	}

	/**
	 * Creates a message embed for the specified track that indicates it has been added to the queue.
	 *
	 * @param track The track for which the embed message is created.
	 * @return The created embed message.
	 */
	override fun createTrackResponseMessage(track: Track): MessageEmbed {
		val context = guildMusicManager.state.context
		val queueSize = guildMusicManager.state.queueTrackScheduler.queue.size
		val trackPosition = if (queueSize == 1) {
			guildMusicManager.bean.i18n.t(I18nAudioSource.NEXT_TRACK_INDEX_MESS, context.guildLanguage)
		} else {
			queueSize.toString()
		}
		val messageBuilder = guildMusicManager.createEmbedBuilder()
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
	 */
	override fun onError(details: AudioLoadFailedDetails) {
		val state = guildMusicManager.state
		// If there are no tracks in the queue and no current track playing, start the leave waiter.
		if (state.queueTrackScheduler.queue.size == 0 && guildMusicManager.cachedPlayer?.track == null) {
			state.clearAudioType()
			guildMusicManager.startLeavingWaiter()
		}
		super.onError(details)
	}
}
