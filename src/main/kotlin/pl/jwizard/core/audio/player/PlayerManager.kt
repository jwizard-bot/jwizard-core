/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import pl.jwizard.core.audio.ExtendedAudioTrackInfo
import pl.jwizard.core.audio.TrackPosition
import pl.jwizard.core.command.CompoundCommandEvent
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import net.dv8tion.jda.api.entities.VoiceChannel

interface PlayerManager {
	fun loadAndPlay(event: CompoundCommandEvent, trackUrl: String, isUrlPattern: Boolean)
	fun pauseTrack(event: CompoundCommandEvent)
	fun resumePausedTrack(event: CompoundCommandEvent)
	fun skipTrack(event: CompoundCommandEvent): ExtendedAudioTrackInfo?
	fun shuffleQueue(event: CompoundCommandEvent)
	fun setTrackRepeat(event: CompoundCommandEvent, repeats: Int)
	fun toggleInfiniteLoopTrack(event: CompoundCommandEvent): Boolean
	fun toggleInfiniteLoopPlaylist(event: CompoundCommandEvent): Boolean
	fun setPlayerVolume(event: CompoundCommandEvent, volume: Int)
	fun skipToTrackPos(event: CompoundCommandEvent, position: Int): AudioTrack
	fun removeTracksFromMember(event: CompoundCommandEvent, memberId: String): MemberRemovedTracksInfo
	fun moveTrackToPos(event: CompoundCommandEvent, position: TrackPosition): AudioTrack
	fun clearQueue(event: CompoundCommandEvent): Int
	fun currentPlayingTrack(event: CompoundCommandEvent): ExtendedAudioTrackInfo?
	fun moveToMemberCurrentVoiceChannel(event: CompoundCommandEvent): VoiceChannel
}
