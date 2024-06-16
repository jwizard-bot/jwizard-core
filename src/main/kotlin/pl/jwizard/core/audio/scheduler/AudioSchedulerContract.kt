/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.scheduler

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason

interface AudioSchedulerContract {
	fun onPause() {}
	fun onResume() {}
	fun onStart()
	fun onEnd(track: AudioTrack, endReason: AudioTrackEndReason)
	fun onException(track: AudioTrack, ex: FriendlyException)
}
