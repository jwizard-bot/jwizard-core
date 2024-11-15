/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClientSupplier
import pl.jwizard.jwc.core.audio.spi.MusicManagersSupplier
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerHandler
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.jda.spi.JdaShardManager
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * An IoC component that aggregates various environment-related beans used in the command processing system.
 *
 * @property environmentBean Access to environment-related properties and configurations.
 * @property i18nBean Manages internationalization settings for localized messages.
 * @property jdaColorStoreBean Handles color settings for JDA interactions.
 * @property eventQueueBean Manages event queue for asynchronous processing of JDA events.
 * @property musicManagersBean Supplies music manager instances for voice channel management.
 * @property distributedAudioClientSupplier Supplies distributed audio client instance for audio streaming.
 * @property jdaShardManager Manages multiple shards of the JDA bot, responsible for handling Discord API interactions.
 * @property guildSettingsEventAction Handles guild settings change events.
 * @property exceptionTrackerHandler The store used to track and log exceptions.
 * @property looselyTransportHandlerBean Handles loosely-typed transport operations between services.
 * @author Miłosz Gilga
 */
@SingletonComponent
class CommandEnvironmentBean(
	val environmentBean: EnvironmentBean,
	val i18nBean: I18nBean,
	val jdaColorStoreBean: JdaColorStoreBean,
	val eventQueueBean: EventQueueBean,
	val musicManagersBean: MusicManagersSupplier,
	val distributedAudioClientSupplier: DistributedAudioClientSupplier,
	val jdaShardManager: JdaShardManager,
	val guildSettingsEventAction: GuildSettingsEventAction,
	val exceptionTrackerHandler: ExceptionTrackerHandler,
	val looselyTransportHandlerBean: LooselyTransportHandlerBean,
)
