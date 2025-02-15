package pl.jwizard.jwc.audio.manager

import pl.jwizard.jwc.audio.client.DistributedAudioClientBean
import pl.jwizard.jwc.audio.spi.RadioStationThumbnailSupplier
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
class MusicManagersBean(
	val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
	val i18n: I18nBean,
	val jdaColorStore: JdaColorsCacheBean,
	val environment: EnvironmentBean,
	val eventQueue: EventQueueBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
	val radioStationThumbnailSupplier: RadioStationThumbnailSupplier,
) {
	// key as guild id and value as music manager related with guild
	private val musicManagers = mutableMapOf<Long, GuildMusicManager>()

	fun getOrCreateMusicManager(
		context: GuildCommandContext,
		future: TFutureResponse,
		audioClient: DistributedAudioClientBean,
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
