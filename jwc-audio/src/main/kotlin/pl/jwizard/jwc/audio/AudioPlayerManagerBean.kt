/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.nico.NicoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.yamusic.YandexMusicAudioSourceManager
import dev.lavalink.youtube.YoutubeAudioSourceManager
import dev.lavalink.youtube.clients.MusicWithThumbnail
import dev.lavalink.youtube.clients.TvHtml5EmbeddedWithThumbnail
import dev.lavalink.youtube.clients.WebWithThumbnail
import org.apache.http.client.config.RequestConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.AudioPlayerManager
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Manages and configures audio player sources for the application.
 *
 * This class extends [DefaultAudioPlayerManager] and implements the [AudioPlayerManager] interface, providing a
 * centralized configuration for different audio source managers.
 *
 * @property environmentBean Provides access to environment configuration properties.
 * @author Miłosz Gilga
 */
@Component
class AudioPlayerManagerBean(
	private val environmentBean: EnvironmentBean
) : AudioPlayerManager, DefaultAudioPlayerManager() {

	companion object {
		private val log = LoggerFactory.getLogger(AudioPlayerManagerBean::class.java)

		/**
		 * Timeout value for HTTP connections in milliseconds.
		 */
		private const val CONNECTION_TIMEOUT = 10000

		/**
		 * Array of audio source managers to be registered with the audio player.
		 * **Registered hierarchically from top to bottom.**
		 */
		private val SOURCE_MANAGERS = arrayOf(
			LocalAudioSourceManager(),
			YoutubeAudioSourceManager(
				MusicWithThumbnail(),
				TvHtml5EmbeddedWithThumbnail(),
				WebWithThumbnail(),
			),
			YandexMusicAudioSourceManager(true),
			SoundCloudAudioSourceManager.createDefault(),
			BandcampAudioSourceManager(),
			VimeoAudioSourceManager(),
			TwitchStreamAudioSourceManager(),
			BeamAudioSourceManager(),
			GetyarnAudioSourceManager(),
			NicoAudioSourceManager(),
			HttpAudioSourceManager()
		)
	}

	/**
	 * Registers all configured audio source managers with the audio player.
	 *
	 * Sets HTTP request configurator with a specific connection timeout and configures
	 * the maximum number of elements per page for YouTube playlists based on environment properties.
	 */
	override fun registerSources() {
		SOURCE_MANAGERS.forEach { registerSourceManager(it) }

		val maxElementsPerPage = environmentBean.getProperty<Int>(BotProperty.JDA_PAGINATION_MAX_ELEMENTS_PER_PAGE)
		setHttpRequestConfigurator { RequestConfig.copy(it).setConnectTimeout(CONNECTION_TIMEOUT).build() }
		source(YoutubeAudioSourceManager::class.java).setPlaylistPageCount(maxElementsPerPage)

		log.info("Register audio sources: {}.", getSourceManagersName())
		log.info(
			"Init audio player manager with: {}s timeout and max elements per page: {}.",
			CONNECTION_TIMEOUT,
			maxElementsPerPage
		)
	}

	/**
	 * Returns a list of names of all registered source managers.
	 *
	 * @return A list of names representing the source managers.
	 */
	fun getSourceManagersName(): List<String> = SOURCE_MANAGERS.map { it.javaClass.simpleName }
}
