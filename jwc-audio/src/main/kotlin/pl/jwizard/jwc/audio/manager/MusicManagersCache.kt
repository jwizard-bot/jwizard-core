package pl.jwizard.jwc.audio.manager

import org.springframework.stereotype.Component
import pl.jwizard.jwc.audio.client.DistributedAudioClientImpl
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.transport.LooselyTransportHandler
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.event.queue.EventQueue
import pl.jwizard.jwc.core.property.GuildEnvironment
import pl.jwizard.jwc.exception.ExceptionTrackerHandler
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.property.BaseEnvironment

@Component
class MusicManagersCache(
	val exceptionTrackerHandler: ExceptionTrackerHandler,
	val i18n: I18n,
	val jdaColorStore: JdaColorsCache,
	val environment: BaseEnvironment,
	val guildEnvironment: GuildEnvironment,
	val eventQueue: EventQueue,
	val looselyTransportHandler: LooselyTransportHandler,
) {
	// key as guild id and value as music manager related with guild
	private val musicManagers = mutableMapOf<Long, GuildMusicManager>()

	fun getOrCreateMusicManager(
		context: GuildCommandContext,
		future: TFutureResponse,
		audioClient: DistributedAudioClientImpl,
	) = synchronized(this) {
		val manager = musicManagers.getOrPut(context.guild.idLong) {
			GuildMusicManager(this, audioClient, context, future)
		}
		// IMPORTANT! state must be updated whenever music manager already exist
		manager.state.updateStateHandlers(future, context)
		manager
	}

	fun getCachedMusicManager(guildId: Long) = musicManagers[guildId]

	fun removeMusicManager(guildId: Long) = musicManagers.remove(guildId)?.dispose()
}
