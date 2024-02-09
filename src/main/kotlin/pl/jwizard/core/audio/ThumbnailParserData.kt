/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

enum class ThumbnailParserData(
	val clazz: Class<*>,
	val callback: (audioTrack: AudioTrack) -> String,
) {
	FOR_YOUTUBE(
		YoutubeAudioTrack::class.java,
		{ track -> "https://img.youtube.com/vi/" + track.identifier + "/0.jpg" }),
	;
}
