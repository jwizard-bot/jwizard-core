/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import pl.jwizard.core.bot.BotProperties
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import org.apache.http.client.config.RequestConfig
import org.springframework.stereotype.Component

@Component
class AudioPlayerManager(
	private val botProperties: BotProperties,
) : DefaultAudioPlayerManager() {

	fun initialize() {
		AudioSourceManagers.registerLocalSource(this)
		AudioSourceManagers.registerRemoteSources(this)
		setHttpRequestConfigurator { RequestConfig.copy(it).setConnectTimeout(CONNECTION_TIMEOUT).build() }
		source(YoutubeAudioSourceManager::class.java).setPlaylistPageCount(botProperties.pagination.maxElementsPerPage)
	}

	companion object {
		private const val CONNECTION_TIMEOUT = 10000
	}
}
