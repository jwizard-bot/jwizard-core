/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import pl.jwizard.core.audio.AudioLoadResultImpl
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.TrackPosition
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.AudioPlayerException
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.exception.UtilException
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.settings.GuildSettings
import pl.jwizard.core.util.BotUtils
import pl.jwizard.core.util.Formatter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.VoiceChannel

@Component
class PlayerManagerFacade(
	private val audioPlayerManager: AudioPlayerManager,
	private val botConfiguration: BotConfiguration,
	private val guildSettings: GuildSettings,
) : PlayerManager, AbstractLoggingBean(PlayerManagerFacade::class) {

	private val musicManagers = mutableMapOf<String, MusicManager>()
	private val lockedGuilds = mutableListOf<String>()

	override fun loadAndPlay(event: CompoundCommandEvent, trackUrl: String, isUrlPattern: Boolean) {
		val musicManager = findMusicManager(event)
		val audioLoadResultHandler = AudioLoadResultImpl(
			musicManager,
			botConfiguration,
			event,
			isUrlPattern,
			lockedGuilds
		)
		event.guild?.audioManager?.isSelfDeafened = true
		audioPlayerManager.loadItemOrdered(musicManager, trackUrl, audioLoadResultHandler)
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
				"member: ${memberWithRemovableTracks.user.asTag}"
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

	fun findMusicManager(event: CompoundCommandEvent): MusicManager {
		val guild = event.guild ?: throw UtilException.UnexpectedException("Guild cannot be null")
		return musicManagers.getOrPut(guild.id) {
			val musicManager = MusicManager(botConfiguration, audioPlayerManager, event, lockedGuilds)
			event.guild.audioManager.sendingHandler = musicManager.audioPlayerSendHandler
			musicManager
		}
	}

	fun findMusicManager(guild: Guild): MusicManager? = musicManagers[guild.id]

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
}
