/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.scheduler

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.Member
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.TrackPosition
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.db.GuildDbProperty
import pl.jwizard.core.db.RadioStationDto
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class SchedulerActions(
	private val botConfiguration: BotConfiguration,
	private val audioScheduler: AudioScheduler,
) : AbstractLoggingBean(SchedulerActions::class) {

	val trackQueue: Queue<AudioTrack> = LinkedList()
	private var totalCountOfRepeats = 0

	var threadsCountToLeave: ScheduledFuture<*>? = null
		private set
	var onClearing: Boolean = false
		private set
	var infiniteRepeating = false
		private set
	var infinitePlaylistRepeating = false
		private set
	var pausedTrack: AudioTrack? = null
	var countOfRepeats = 0
	var nextTrackInfoDisabled = false
	var radioStationDto: RadioStationDto? = null

	internal fun addToQueueAndOffer(audioTrack: AudioTrack) {
		if (!audioScheduler.audioPlayer.startTrack(audioTrack, true)) {
			trackQueue.offer(audioTrack)
		}
	}

	fun nextTrack() {
		val audioTrack = trackQueue.poll()
		if (audioTrack != null) {
			audioScheduler.audioPlayer.startTrack(audioTrack, false)
		}
	}

	fun skipToPosition(position: Int) {
		var audioTrack: AudioTrack? = null
		for (i in 0..position) {
			audioTrack = trackQueue.poll()
		}
		if (audioTrack != null) {
			audioScheduler.audioPlayer.startTrack(audioTrack, false)
		}
	}

	fun moveToPosition(positions: TrackPosition): AudioTrack {
		val copiedTracks = ArrayList(trackQueue)
		val selectedTrack = copiedTracks.removeAt(positions.previous - 1)
		copiedTracks.add(positions.selected - 1, selectedTrack)
		trackQueue.clear()
		trackQueue.addAll(copiedTracks)
		return selectedTrack
	}

	fun removeAllTracksFromMember(member: Member): List<AudioTrack> {
		return ArrayList(trackQueue)
			.filter { (it.userData as Member).id == member.id }
			.mapNotNull { trackQueue.poll() }
	}

	fun checkIfAllTracksIsFromSelectedMember(member: Member): Boolean {
		if (trackQueue.isEmpty()) {
			if (audioScheduler.audioPlayer.playingTrack == null) {
				return false
			}
			val sender = audioScheduler.audioPlayer.playingTrack.userData as Member
			return sender.id == member.id
		}
		return trackQueue.all { (it.userData as Member).id == member.id }
	}

	fun clearAndDestroy(showMessage: Boolean) {
		onClearing = true
		audioScheduler.audioPlayer.isPaused = false
		audioScheduler.audioPlayer.stopTrack()
		trackQueue.clear()

		pausedTrack = null
		countOfRepeats = 0
		totalCountOfRepeats = 0
		infiniteRepeating = false
		nextTrackInfoDisabled = false
		infinitePlaylistRepeating = false

		if (showMessage) {
			val messageEmbed = CustomEmbedBuilder(botConfiguration, audioScheduler.event).buildBaseMessage(
				placeholder = I18nResLocale.LEAVE_EMPTY_CHANNEL
			)
			audioScheduler.event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)
		}
		onClearing = false
		jdaLog.info(audioScheduler.event, "Remove playing track and clear queue")
	}

	fun leaveAndSendMessageAfterInactivity() {
		val timeToLeaveChannel = botConfiguration.guildSettingsSupplier
			.fetchDbProperty(GuildDbProperty.LEAVE_NO_TRACKS_SEC, audioScheduler.event.guildId, Int::class)
		threadsCountToLeave = botConfiguration.threadPool.schedule({
			val messageEmbed = CustomEmbedBuilder(botConfiguration, audioScheduler.event).buildBaseMessage(
				I18nResLocale.LEAVE_END_PLAYBACK_QUEUE,
				params = mapOf("elapsed" to DateUtils.convertSecToMin(timeToLeaveChannel.toLong()))
			)
			if (!audioScheduler.lockedGuilds.contains(audioScheduler.event.guildId)) {
				clearAndDestroy(false)

				val guild = audioScheduler.event.dataSender?.guild
				botConfiguration.threadPool.submit { guild?.audioManager?.closeAudioConnection() }
				log.info("Audio connection threadpool for guild: {} was closed", Formatter.guildTag(guild))

				audioScheduler.event.instantlySendEmbedMessage(messageEmbed, legacyTransport = true)
				jdaLog.info(
					audioScheduler.event,
					"Leaved voice channel after $timeToLeaveChannel seconds of inactivity"
				)
			}
		}, timeToLeaveChannel.toLong(), TimeUnit.SECONDS)
	}

	fun getAverageTracksDuration(): Long = trackQueue
		.map { it.duration }
		.average()
		.toLong()

	fun getTrackByPosition(position: Int): AudioTrack = ArrayList(trackQueue)[position - 1]

	fun checkInvertedTrackPosition(position: Int) = position <= 0 || position > trackQueue.size

	fun currentRepeat() = (totalCountOfRepeats - countOfRepeats) + 1

	fun checkIfMemberAddAnyTracksToQueue(member: Member) = trackQueue
		.any { (it.userData as Member).id == member.id }

	fun toggleInfiniteRepeating(): Boolean {
		infiniteRepeating = !infiniteRepeating
		return infiniteRepeating
	}

	fun toggleInfinitePlaylistRepeating(): Boolean {
		infinitePlaylistRepeating = !infinitePlaylistRepeating
		return infinitePlaylistRepeating
	}

	fun updateCountOfRepeats(count: Int) {
		countOfRepeats = count
		totalCountOfRepeats = count
		if (count > 0) {
			nextTrackInfoDisabled = true
		}
	}

	fun getPausedTrackInfo(): ExtendedAudioTrackInfo = pausedTrack
		?.let { ExtendedAudioTrackInfo(it) }
		?: throw AudioPlayerException.TrackIsNotPausedException(audioScheduler.event)
}
