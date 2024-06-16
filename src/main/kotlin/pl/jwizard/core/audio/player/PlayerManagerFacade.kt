/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import pl.jwizard.core.audio.*
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.db.RadioStationDto
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.BotUtils
import pl.jwizard.core.util.Formatter
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Component
class PlayerManagerFacade(
	private val audioPlayerManager: AudioPlayerManager,
	private val botConfiguration: BotConfiguration,
) : PlayerManager, AbstractLoggingBean(PlayerManager::class) {

	private val musicManagers = mutableMapOf<String, MusicManager>()
	private val lockedGuilds = mutableListOf<String>()

	override fun loadAndPlay(event: CompoundCommandEvent, trackUrl: String, isUrlPattern: Boolean) {
		val musicManager = findMusicManager(event) // not creating, because it is instantiated in AbstractMusicCmd

		switchAudioTrackScheduler(event, musicManager, AudioSourceType.TRACK)
		musicManager.audioScheduler.setCompoundEvent(event)

		val audioLoadResultHandler = AudioLoadResultImpl(
			musicManager,
			botConfiguration,
			event,
			isUrlPattern,
			lockedGuilds
		)
		event.invokedBySender = true // set hook to inform, that sending of this message was invoked by user
		event.guild?.audioManager?.isSelfDeafened = true
		audioPlayerManager.loadItemOrdered(musicManager, trackUrl, audioLoadResultHandler)
	}

	override fun loadAndStream(event: CompoundCommandEvent, radioStation: RadioStationDto) {
		val musicManager = findMusicManager(event)

		switchAudioTrackScheduler(event, musicManager, AudioSourceType.STREAM)
		musicManager.audioScheduler.setCompoundEvent(event)

		musicManager.actions.radioStationDto = radioStation // set radio station and block default queue

		// check primary stream url, if itn't working, swich to proxy stream url
		val client = HttpClient.newHttpClient()
		val request = HttpRequest.newBuilder()
			.uri(URI.create(radioStation.streamUrl))
			.method(HttpMethod.HEAD.name(), HttpRequest.BodyPublishers.noBody())
			.build()

		val response = client.send(request, HttpResponse.BodyHandlers.discarding())
		// determinate stream url base availibility
		val streamUrl = if (response.statusCode() == 200) {
			radioStation.streamUrl
		} else {
			radioStation.proxyStreamUrl
		}
		event.guild?.audioManager?.isSelfDeafened = true
		val streamLoadResultHandler = StreamLoadResultImpl(
			musicManager,
			botConfiguration,
			event,
			radioStation
		)
		audioPlayerManager.loadItem(streamUrl, streamLoadResultHandler)
	}

	override fun pauseTrack(event: CompoundCommandEvent) {
		val musicManager = findMusicManagerWithPermissions(event)
		musicManager.audioPlayer.isPaused = true
		jdaLog.info(event, "Audio player is stopped")
	}

	override fun resumePausedTrack(event: CompoundCommandEvent) {
		val musicManager = findMusicManager(event)
		val pausedTrack = musicManager.actions.getPausedTrackInfo()
		if (event.checkIfInvokerIsNotSenderOrAdmin(pausedTrack)) {
			throw AudioPlayerException.InvokerIsNotTrackSenderOrAdminException(event)
		}
		musicManager.audioPlayer.isPaused = false
		jdaLog.info(event, "Audio player is resumed")
	}

	override fun skipTrack(event: CompoundCommandEvent): ExtendedAudioTrackInfo? {
		val musicManager = findMusicManagerWithPermissions(event)
		val skippedTrack = currentPlayingTrack(event)
		if (musicManager.queue.isEmpty()) {
			musicManager.audioPlayer.stopTrack()
		} else {
			musicManager.actions.nextTrack()
		}
		jdaLog.info(event, "Current playing track ${skippedTrack?.title} was skipped")
		return skippedTrack
	}

	override fun shuffleQueue(event: CompoundCommandEvent) {
		val musicManager = findMusicManager(event)
		(musicManager.queue as MutableList<*>).shuffle()
		jdaLog.info(event, "Current queue tracks was shuffled")
	}

	override fun setTrackRepeat(event: CompoundCommandEvent, repeats: Int) {
		val musicManager = findMusicManagerWithPermissions(event)
		musicManager.actions.updateCountOfRepeats(repeats)
		val currentPlayingTrack = currentPlayingTrack(event)?.title
		val logInfo: String = if (repeats == 0) {
			"Repeating of current playing track $currentPlayingTrack was removed"
		} else {
			"Repeating of current playing track $currentPlayingTrack will be repeating $repeats times"
		}
		jdaLog.info(event, logInfo)
	}

	override fun toggleInfiniteLoopTrack(event: CompoundCommandEvent): Boolean {
		val musicManager = findMusicManagerWithPermissions(event)
		val isRepeating = musicManager.actions.toggleInfiniteRepeating()
		jdaLog.info(
			event, "Current infinite playing was turn ${Formatter.boolStr(isRepeating)} " +
				"for track: ${currentPlayingTrack(event)?.title}"
		)
		return isRepeating
	}

	override fun toggleInfiniteLoopPlaylist(event: CompoundCommandEvent): Boolean {
		val musicManager = findMusicManager(event)
		if (musicManager.queue.isEmpty()) {
			throw AudioPlayerException.TrackQueueIsEmptyException(event)
		}
		val isTurnOn = musicManager.actions.toggleInfinitePlaylistRepeating()
		jdaLog.info(event, "Current playlist was turn ${Formatter.boolStr(isTurnOn)} for infinite repeating")
		return isTurnOn
	}

	override fun setPlayerVolume(event: CompoundCommandEvent, volume: Int) {
		val musicManager = findMusicManager(event)
		musicManager.audioPlayer.volume = volume
		jdaLog.info(event, "Audio player volume was set to $volume volume units")
	}

	override fun skipToTrackPos(event: CompoundCommandEvent, position: Int): AudioTrack {
		val musicManager = findMusicManager(event)
		if (musicManager.queue.isEmpty()) {
			throw AudioPlayerException.TrackQueueIsEmptyException(event)
		}
		musicManager.actions.skipToPosition(position)
		val currentPlaying = musicManager.audioPlayer.playingTrack
		jdaLog.info(
			event,
			"$position tracks in queue was skipped and started playing track ${currentPlaying.info.title}"
		)
		return currentPlaying
	}

	override fun removeTracksFromMember(event: CompoundCommandEvent, memberId: String): MemberRemovedTracksInfo {
		val memberWithRemovableTracks = BotUtils.checkIfMemberInGuildExist(event, memberId)
		val musicManager = findMusicManager(event)
		if (musicManager.actions.checkIfMemberAddAnyTracksToQueue(memberWithRemovableTracks)) {
			throw UserException.UserNotAddedTracksToQueueException(event)
		}
		val removedTracks = musicManager.actions.removeAllTracksFromMember(memberWithRemovableTracks)
		jdaLog.info(
			event, "Following tracks was removed $removedTracks added by " +
				"member: ${memberWithRemovableTracks.user.name}"
		)
		return MemberRemovedTracksInfo(
			member = memberWithRemovableTracks,
			removedTracks,
		)
	}

	override fun moveTrackToPos(event: CompoundCommandEvent, position: TrackPosition): AudioTrack {
		val musicManager = findMusicManager(event)
		val actions = musicManager.actions
		if (musicManager.queue.isEmpty()) {
			throw AudioPlayerException.TrackQueueIsEmptyException(event)
		} else if (position.checkBounds(actions)) {
			throw AudioPlayerException.TrackPositionOutOfBoundsException(event, musicManager.queue.size)
		} else if (position.isSamePosition()) {
			throw AudioPlayerException.TrackPositionsIsTheSameException(event)
		}
		val movedTrack = musicManager.actions.moveToPosition(position)
		jdaLog.info(
			event, "Audio track: ${movedTrack.info.title} was moved from ${position.previous} " +
				"to ${position.selected} position in queue"
		)
		return movedTrack
	}

	override fun clearQueue(event: CompoundCommandEvent): Int {
		val musicManager = findMusicManager(event)
		val countOfTracksInQueue = musicManager.queue.size
		musicManager.actions.trackQueue.clear()
		jdaLog.info(event, "Queue was cleared. Removed $countOfTracksInQueue audio tracks from queue")
		return countOfTracksInQueue
	}

	override fun currentPlayingTrack(event: CompoundCommandEvent): ExtendedAudioTrackInfo? {
		val musicManager = findMusicManager(event)
		if (musicManager.audioPlayer.playingTrack != null) {
			return ExtendedAudioTrackInfo(musicManager.audioPlayer.playingTrack)
		}
		return null
	}

	override fun moveToMemberCurrentVoiceChannel(event: CompoundCommandEvent): VoiceChannel {
		val voiceChannelWithMember = event.guild?.voiceChannels
			?.find { it.members.contains(event.member) }
			?: throw UserException.UserOnVoiceChannelNotFoundException(event)
		if (voiceChannelWithMember.members.contains(event.botMember)) {
			throw UserException.UserIsAlreadyWithBotException(event)
		}
		event.guild.moveVoiceMember(event.botMember!!, voiceChannelWithMember).complete()
		jdaLog.info(event, "Bot was successfully moved to channel ${voiceChannelWithMember.name}")
		return voiceChannelWithMember
	}

	override fun findMusicManager(event: CompoundCommandEvent): MusicManager = musicManagers.getOrPut(event.guildId) {
		val musicManager = MusicManager(botConfiguration, event, audioPlayerManager, lockedGuilds)
		event.guild?.audioManager?.sendingHandler = musicManager.audioPlayerSendHandler
		musicManagers[event.guildId] = musicManager
		return musicManager
	}

	override fun findMusicManager(guildId: String): MusicManager? = musicManagers[guildId]

	private fun findMusicManagerWithPermissions(event: CompoundCommandEvent): MusicManager {
		val musicManager = findMusicManager(event)
		val playingTrack = musicManager.audioPlayer.playingTrack
		if (playingTrack == null) {
			throw AudioPlayerException.TrackIsNotPlayingException(event)
		} else if (event.checkIfInvokerIsNotSenderOrAdmin(ExtendedAudioTrackInfo(playingTrack))) {
			throw AudioPlayerException.InvokerIsNotTrackSenderOrAdminException(event)
		}
		return musicManager
	}

	private fun switchAudioTrackScheduler(
		event: CompoundCommandEvent,
		musicManager: MusicManager,
		audioSourceType: AudioSourceType
	) {
		musicManager.audioScheduler.setAudioScheduler(audioSourceType)
		jdaLog.info(event, "Changing audio scheduler instance to ${audioSourceType.name} type")
	}
}
