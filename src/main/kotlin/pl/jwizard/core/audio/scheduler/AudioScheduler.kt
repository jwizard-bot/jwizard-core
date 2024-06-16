/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.scheduler

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import pl.jwizard.core.audio.AudioSourceType
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent

class AudioScheduler(
	val botConfiguration: BotConfiguration,
	var event: CompoundCommandEvent,
	val audioPlayer: AudioPlayer,
	val lockedGuilds: MutableList<String>,
) : AudioEventAdapter() {

	val schedulerActions = SchedulerActions(botConfiguration, this)

	private var facade: AudioSchedulerContract? = null

	override fun onPlayerPause(player: AudioPlayer?) {
		facade?.onPause()
	}

	override fun onPlayerResume(player: AudioPlayer?) {
		facade?.onResume()
	}

	override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
		facade?.onStart()
	}

	override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack, endReason: AudioTrackEndReason) {
		facade?.onEnd(track, endReason)
	}

	override fun onTrackException(player: AudioPlayer?, track: AudioTrack, exception: FriendlyException) {
		facade?.onException(track, exception)
	}

	fun setCompoundEvent(event: CompoundCommandEvent) {
		this.event = event
	}

	fun setAudioScheduler(audioSourceType: AudioSourceType) {
		facade = audioSourceType.getInstance(this)
	}

	fun isStreamFacade(): Boolean = facade != null && facade is StreamSchedulerFacade
}
