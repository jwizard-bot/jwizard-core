package pl.jwizard.jwc.audio.event

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent
import pl.jwizard.jwc.audio.client.DistributedAudioClientBean
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.spi.ChannelListenerGuard
import pl.jwizard.jwc.core.jvm.thread.JvmFixedThreadExecutor
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger
import java.time.Instant

@SingletonComponent
class AudioChannelsListenerGuardBean(
	private val jdaShardManager: JdaShardManagerBean,
	private val environment: EnvironmentBean,
	private val musicManagers: MusicManagersBean,
	private val audioClient: DistributedAudioClientBean,
) : ChannelListenerGuard, JvmFixedThreadExecutor() {

	companion object {
		private val log = logger<AudioChannelsListenerGuardBean>()

		// interval in seconds at which the executor service runs to check voice channels
		private const val INTERVAL_TICK_SEC = 5L
	}

	// guild Ids to time when the guild's voice channel was last detected as empty
	private final val aloneFromTime = mutableMapOf<Long, Instant>()

	override fun initThreadPool() {
		start(intervalSec = INTERVAL_TICK_SEC)
		log.info("Start listening users voice channels with interval: {}s.", INTERVAL_TICK_SEC)
	}

	fun onEveryVoiceUpdate(event: GuildVoiceUpdateEvent) {
		val guild = event.guild
		val botVoiceState = guild.selfMember.voiceState
		if (botVoiceState?.inAudioChannel() == true) {
			val channel = botVoiceState.channel?.asVoiceChannel()
			val isAloneOnVoiceChannel = channel?.members?.none {
				it.voiceState?.isDeafened == false && !it.user.isBot
			} == true
			val isAlonePrevious = aloneFromTime.containsKey(guild.idLong)
			if (!isAloneOnVoiceChannel && isAlonePrevious) {
				aloneFromTime.remove(guild.idLong)
			}
			if (isAloneOnVoiceChannel && !isAlonePrevious) {
				aloneFromTime[guild.idLong] = Instant.now()
			}
		}
	}

	override fun executeJvmThread() {
		// set to store guilds that should be removed from tracking
		val removeFromGuild = mutableSetOf<Long>()

		// loop through all tracked guilds with their last activity timestamp
		for ((guildId, time) in aloneFromTime) {
			val guild = jdaShardManager.getGuildById(guildId)

			// if the guild is null, meaning the bot was removed from the server, mark it for removal
			if (guild == null) {
				removeFromGuild.add(guildId)
				continue
			}
			val maxInactivity = environment
				.getGuildProperty<Long>(GuildProperty.LEAVE_EMPTY_CHANNEL_SEC, guildId)

			// if the bot is still within the allowed inactivity time, skip processing
			if (time.epochSecond > (Instant.now().epochSecond - maxInactivity)) {
				continue
			}
			// otherwise, remove bot from channel and clean audio queue
			val musicManager = musicManagers.getCachedMusicManager(guildId)

			if (musicManager != null) {
				// leave the channel only if the bot is still on it (did not leave after 2 minutes of
				// inactivity)
				if (audioClient.inAudioChannel(musicManager.state.context.selfMember)) {
					val message = musicManager.createEmbedBuilder()
						.setDescription(I18nResponseSource.LEAVE_EMPTY_CHANNEL)
						.setColor(JdaColor.PRIMARY)
						.build()
					musicManager.state.audioScheduler.stopAndDestroy().subscribe()
					audioClient.disconnectWithAudioChannel(guild)
					log.jdaInfo(
						musicManager.state.context,
						"Leave voice channel in guild: {}. Cause: not found any active user.",
						guild.qualifier
					)
					musicManager.sendMessage(message)
				}
				// remove music manager regardless of the circumstances
				musicManagers.removeMusicManager(guildId)
			}
			removeFromGuild.add(guildId)
		}
		for (guild in removeFromGuild) {
			aloneFromTime.remove(guild)
		}
	}
}
