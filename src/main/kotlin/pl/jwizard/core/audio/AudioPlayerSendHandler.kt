/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.ChannelType
import java.nio.ByteBuffer

class AudioPlayerSendHandler(
	private val guild: Guild?,
	private val audioPlayer: AudioPlayer,
) : AudioSendHandler {

	private var audioFrame: AudioFrame? = null

	fun isInPlayingMode(): Boolean {
		val isActive = guild?.selfMember?.voiceState?.channel?.type == ChannelType.VOICE
		return audioPlayer.playingTrack != null && isActive
	}

	override fun canProvide(): Boolean {
		audioFrame = audioPlayer.provide()
		return audioFrame != null
	}

	override fun provide20MsAudio(): ByteBuffer? = ByteBuffer.wrap(audioFrame?.data)
	override fun isOpus(): Boolean = true
}
