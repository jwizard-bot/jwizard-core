/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler
import java.nio.ByteBuffer

/**
 * A handler for sending audio data to Discord.
 *
 * This class implements the [AudioSendHandler] interface from JDA to provide audio data to the Discord voice server.
 * It uses an [AudioPlayer] from the Lavaplayer library to retrieve audio frames and send them in the appropriate
 * format.
 *
 * @property audioPlayer The [AudioPlayer] instance used to provide audio frames.
 * @author Miłosz Gilga
 */
class AudioPlayerSendHandler(private val audioPlayer: AudioPlayer) : AudioSendHandler {

	/**
	 * The current audio frame that is to be sent.
	 */
	private var audioFrame: AudioFrame? = null

	/**
	 * Checks if audio data is available to be provided.
	 *
	 * This method attempts to retrieve an audio frame from the [audioPlayer]. If an audio frame is available, it will
	 * be used in subsequent calls to [provide20MsAudio]. It returns `true` if an audio frame is available,
	 * otherwise `false`.
	 *
	 * @return `true` if an audio frame is available, `false` otherwise.
	 */
	override fun canProvide(): Boolean {
		audioFrame = audioPlayer.provide()
		return audioFrame != null
	}

	/**
	 * Provides audio data to be sent to Discord.
	 *
	 * This method is called to obtain a 20ms chunk of audio data. The data is returned as a [ByteBuffer] containing
	 * the audio frame's data. If no audio frame is available, it returns `null`.
	 *
	 * @return A [ByteBuffer] containing the audio data, or `null` if no data is available.
	 */
	override fun provide20MsAudio(): ByteBuffer? = ByteBuffer.wrap(audioFrame?.data)

	/**
	 * Checks if the audio data is in the Opus format.
	 *
	 * This method always returns `true` because Discord requires audio data to be in the Opus format.
	 *
	 * @return `true`, indicating that the audio data is in Opus format.
	 */
	override fun isOpus(): Boolean = true
}
