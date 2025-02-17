package pl.jwizard.jwc.api

import org.springframework.stereotype.Component
import pl.jwizard.jwc.audio.client.DistributedAudioClientImpl
import pl.jwizard.jwc.audio.manager.MusicManagersCache
import pl.jwizard.jwc.core.jda.JdaShardManager
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCache
import pl.jwizard.jwc.core.jda.event.queue.EventQueue
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.property.GuildEnvironment
import pl.jwizard.jwc.exception.ExceptionTrackerHandler
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.property.BaseEnvironment

@Component
internal class CommandEnvironment(
	val environment: BaseEnvironment,
	val guildEnvironment: GuildEnvironment,
	val i18n: I18n,
	val jdaColorStore: JdaColorsCache,
	val eventQueue: EventQueue,
	val musicManagers: MusicManagersCache,
	val audioClient: DistributedAudioClientImpl,
	val jdaShardManager: JdaShardManager,
	val guildSettingsEventAction: GuildSettingsEventAction,
	val exceptionTrackerHandler: ExceptionTrackerHandler,
	val botEmojisCache: BotEmojisCache,
)
