package pl.jwizard.jwc.api

import pl.jwizard.jwc.audio.client.DistributedAudioClientBean
import pl.jwizard.jwc.audio.manager.MusicManagersBean
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCacheBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
class CommandEnvironmentBean(
	val environment: EnvironmentBean,
	val i18n: I18nBean,
	val jdaColorStore: JdaColorsCacheBean,
	val eventQueue: EventQueueBean,
	val musicManagers: MusicManagersBean,
	val audioClient: DistributedAudioClientBean,
	val jdaShardManager: JdaShardManagerBean,
	val guildSettingsEventAction: GuildSettingsEventAction,
	val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
	val botEmojisCache: BotEmojisCacheBean,
)
