package pl.jwizard.jwc.audio.loader

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.audio.gateway.event.onload.*
import pl.jwizard.jwc.audio.gateway.player.track.AudioSender
import pl.jwizard.jwc.audio.gateway.player.track.Track
import pl.jwizard.jwc.audio.loader.spinner.TrackMenuOption
import pl.jwizard.jwc.audio.loader.spinner.TrackSelectSpinnerMenu
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.util.logger

internal class QueueTrackLoader(
	private val guildMusicManager: GuildMusicManager,
) : AudioCompletableFutureLoader(guildMusicManager) {
	companion object {
		private val log = logger<QueueTrackLoader>()
	}

	private val guildProperties = guildMusicManager.bean.guildEnvironment.getGuildMultipleProperties(
		guildProperties = listOf(
			GuildProperty.RANDOM_AUTO_CHOOSE_TRACK,
			GuildProperty.TIME_AFTER_AUTO_CHOOSE_SEC,
			GuildProperty.MAX_TRACKS_TO_CHOOSE,
		),
		guildId = guildMusicManager.state.context.guild.idLong,
	)

	override fun onCompletableTrackLoaded(result: KTrackLoadedEvent, future: TFutureResponse) {
		val context = guildMusicManager.state.context
		result.track.setSenderData(AudioSender(context.author.idLong))

		onEnqueueTrack(result.track)
		log.jdaInfo(
			context,
			"Added to queue: %s track by: %s.",
			result.track.qualifier,
			context.author.qualifier,
		)
		val response = CommandResponse.Builder()
			.addEmbedMessages(createTrackResponseMessage(result.track))
			.build()
		future.complete(response)
	}

	override fun onCompletableSearchResultLoaded(
		result: KSearchResultEvent,
		future: TFutureResponse
	) {
		if (result.tracks.isEmpty()) {
			onError(
				AudioLoadFailedDetails.Builder()
					.setLogMessage("Unable to find any audio track.")
					.setI18nLocaleSource(I18nExceptionSource.NOT_FOUND_TRACK)
					.build()
			)
			return
		}
		for (track in result.tracks) {
			// IMPORTANT! add sender data to every track
			track.setSenderData(AudioSender(guildMusicManager.state.context.author.idLong))
		}
		val trackSelectSpinnerMenu = TrackSelectSpinnerMenu(
			guildMusicManager,
			onEnqueueTrack = ::onEnqueueTrack, // on select track callback
			createTrackResponseMessage = ::createTrackResponseMessage,
			options = result.tracks.map { TrackMenuOption(it) },
			guildProperties,
		)
		val (message, components) = trackSelectSpinnerMenu.createMenuComponent(
			i18n = guildMusicManager.bean.i18n,
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

		log.jdaInfo(
			context,
			"Added to queue: %s tracks by: %s.",
			result.tracks.size,
			context.author.qualifier
		)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()
		future.complete(response)
	}

	override fun onCompletableLoadFailed(result: KLoadFailedEvent) = AudioLoadFailedDetails.Builder()
		.setLogMessage(
			"Unexpected exception during load audio track/playlist. Cause: %s.",
			result.exception.message
		)
		.setI18nLocaleSource(I18nExceptionSource.ISSUE_WHILE_LOADING_TRACK)
		.build()

	override fun onCompletableNoMatches(result: KNoMatchesEvent) = AudioLoadFailedDetails.Builder()
		.setLogMessage("Unable to find any audio track/playlist.")
		.setI18nLocaleSource(I18nExceptionSource.NOT_FOUND_TRACK)
		.build()

	private fun onEnqueueTrack(track: Track) {
		guildMusicManager.state.audioScheduler.loadContent(listOf(track))
	}

	private fun createTrackResponseMessage(track: Track): MessageEmbed {
		val context = guildMusicManager.state.context
		val queueSize = guildMusicManager.state.queueTrackScheduler.queue.size
		val trackPosition = if (queueSize == 1) {
			guildMusicManager.bean.i18n.t(I18nAudioSource.NEXT_TRACK_INDEX_MESS, context.language)
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
