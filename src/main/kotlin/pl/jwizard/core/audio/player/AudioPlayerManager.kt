/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio.player

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.clients.AndroidWithThumbnail
import dev.lavalink.youtube.clients.MusicWithThumbnail
import dev.lavalink.youtube.clients.WebWithThumbnail
import org.apache.http.client.config.RequestConfig
import org.springframework.stereotype.Component
import pl.jwizard.core.bot.BotProperties

@Component
class AudioPlayerManager(
	private val botProperties: BotProperties,
) : DefaultAudioPlayerManager() {

	@Suppress("DEPRECATION")
	fun initialize() {
		AudioSourceManagers.registerLocalSource(this)
		registerCustomYoutubeSourceManager()
		AudioSourceManagers.registerRemoteSources(
			this,
			com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager::class.java
		)
		setHttpRequestConfigurator { RequestConfig.copy(it).setConnectTimeout(CONNECTION_TIMEOUT).build() }
		source(YoutubeAudioSourceManager::class.java).setPlaylistPageCount(botProperties.pagination.maxElementsPerPage)
	}

	fun registerCustomYoutubeSourceManager() {
		val manager = YoutubeAudioSourceManager(MusicWithThumbnail(), WebWithThumbnail(), AndroidWithThumbnail())
		registerSourceManager(manager)
	}

	companion object {
		private const val CONNECTION_TIMEOUT = 10000
	}
}
