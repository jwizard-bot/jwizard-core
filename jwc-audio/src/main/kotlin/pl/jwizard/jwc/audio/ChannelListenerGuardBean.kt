/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.JdaThreadExecutor
import pl.jwizard.jwc.core.jda.stereotype.ChannelListenerGuard
import pl.jwizard.jwc.core.jda.stereotype.JdaInstance
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.GuildProperty
import pl.jwizard.jwc.core.util.Formatter
import java.time.Instant

/**
 * Monitors voice channels in guilds to detect when they become empty and performs actions accordingly.
 * This component extends [JdaThreadExecutor] to periodically check voice channels and uses [ChannelListenerGuard]
 * to handle voice channel events.
 *
 * @property jdaInstance Provide access to the JDA API, used to retrieve guild information.
 * @property environmentBean Provides access to application properties.
 * @property playerManagersBean Manage audio players and their connections in guilds.
 * @author Miłosz Gilga
 * @see ChannelListenerGuard
 * @see JdaThreadExecutor
 */
@Component
class ChannelListenerGuardBean(
	private val jdaInstance: JdaInstance,
	private val environmentBean: EnvironmentBean,
	private val playerManagersBean: PlayerManagersBean,
) : ChannelListenerGuard, JdaThreadExecutor(MAX_THREADS) {

	companion object {
		private val log = LoggerFactory.getLogger(ChannelListenerGuardBean::class.java)

		/**
		 * The maximum number of threads used for the executor service.
		 */
		private const val MAX_THREADS = 5

		/**
		 * The interval in seconds at which the executor service runs to check voice channels.
		 */
		private const val INTERVAL_TICK_SEC = 5L
	}

	/**
	 * Maps guild IDs to the time when the guild's voice channel was last detected as empty.
	 * This is used to track the inactivity of voice channels.
	 */
	private final val aloneFromTime = mutableMapOf<String, Instant>()

	/**
	 * Initializes the thread pool and starts the executor service with the configured interval.
	 */
	override fun initThreadPool() {
		start(intervalSec = INTERVAL_TICK_SEC)
		log.info(
			"Start listening users voice channels with interval: {}s and threads count: {}.",
			INTERVAL_TICK_SEC, MAX_THREADS
		)
	}

	/**
	 * Handles voice update events to detect changes in the state of voice channels.
	 * Updates the [aloneFromTime] map based on whether the voice channel is empty or not.
	 *
	 * @param event The [GuildVoiceUpdateEvent] containing information about the voice state update.
	 */
	override fun onEveryVoiceUpdate(event: GuildVoiceUpdateEvent) {
		val guild = event.guild
		guild.audioManager.sendingHandler?.let {
			val isAlone = isAloneOnChannel(guild)
			val isAlonePrevious = aloneFromTime.containsKey(guild.id)
			if (!isAlone && isAlonePrevious) {
				aloneFromTime.remove(guild.id)
			}
			if (isAlone && !isAlonePrevious) {
				aloneFromTime[guild.id] = Instant.now()
			}
		}
	}

	/**
	 * Periodically checks the [aloneFromTime] map to determine if any guilds have been empty for too long.
	 * If a guild's voice channel has been empty beyond the allowed inactivity period, it disconnects the audio player
	 * and logs the action.
	 *
	 * Removes guilds that were found to be empty from the [aloneFromTime] map.
	 */
	override fun run() {
		val removeFromGuild = mutableSetOf<String>()
		for ((guildId, time) in aloneFromTime) {
			val guild = jdaInstance.getGuildById(guildId)
			if (guild == null) {
				removeFromGuild.add(guildId)
				continue
			}
			val maxInactivity = environmentBean.getGuildProperty<Long>(GuildProperty.LEAVE_EMPTY_CHANNEL_SEC, guildId)
			if (time.epochSecond > (Instant.now().epochSecond - maxInactivity)) {
				continue
			}
			val playerManager = playerManagersBean.getManager(guildId)
			playerManager?.destroyAndDisconnect()

			log.info("Leave voice channel in guild: {}. Cause: not found any active user.", Formatter.guildQualifier(guild))
			removeFromGuild.add(guildId)
		}
		removeFromGuild.forEach { aloneFromTime.remove(it) }
	}

	/**
	 * Checks if the bot is the only member in the voice channel of the specified guild.
	 *
	 * @param guild The [Guild] whose voice channel is being checked.
	 * @return True if the bot is the only member in the voice channel; false otherwise.
	 */
	private fun isAloneOnChannel(guild: Guild): Boolean {
		val connectedChannel = guild.audioManager.connectedChannel ?: return false
		return connectedChannel.members.none { it.voiceState?.isDeafened == false && !it.user.isBot }
	}
}
