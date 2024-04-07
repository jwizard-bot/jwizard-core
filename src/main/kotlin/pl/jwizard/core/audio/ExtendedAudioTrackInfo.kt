/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import net.dv8tion.jda.api.entities.Member

class ExtendedAudioTrackInfo(
	val audioTrack: AudioTrack
) : AudioTrackInfo(
	audioTrack.info.title,
	audioTrack.info.author,
	audioTrack.info.length,
	audioTrack.info.identifier,
	audioTrack.info.isStream,
	audioTrack.info.uri,
) {
	val thumbnailUrl = ThumbnailParserData.entries
		.filter { it.clazz.isInstance(audioTrack) }
		.map { it.callback(audioTrack) }
		.firstOrNull()

	val approximateTime get() = audioTrack.duration - audioTrack.position
	val timestamp get() = audioTrack.position
	val maxDuration get() = audioTrack.duration
	val sender get() = (audioTrack.userData as Member).user
}
