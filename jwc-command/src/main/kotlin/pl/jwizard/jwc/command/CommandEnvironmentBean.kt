/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClientSupplier
import pl.jwizard.jwc.core.audio.spi.MusicManagersSupplier
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerHandler
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwc.radio.spi.RadioStationSupplier
import pl.jwizard.jwl.i18n.I18nBean

/**
 * A Spring component that aggregates various environment-related beans used in the command processing system.
 *
 * @property environmentBean Access to environment-related properties and configurations.
 * @property i18nBean Manages internationalization settings for localized messages.
 * @property jdaColorStoreBean Handles color settings for JDA interactions.
 * @property eventQueueBean Manages event queue for asynchronous processing of JDA events.
 * @property musicManagersBean Supplies music manager instances for voice channel management.
 * @property distributedAudioClientSupplier Supplies distributed audio client instance for audio streaming.
 * @property jdaInstance Provides access to the JDA instance for Discord interaction.
 * @property guildSettingsEventAction Handles guild settings change events.
 * @property radioStationSupplier Supplies instances related to radio station management.
 * @property radioPlaybackMappersCache Caches radio playback mappers for efficient access.
 * @property commandDataSupplier Supplies metadata and definitions for bot commands.
 * @property commandsCacheBean Caches command-related data to improve command lookup performance.
 * @property exceptionTrackerHandler The store used to track and log exceptions.
 * @property looselyTransportHandlerBean Handles loosely-typed transport operations between services.
 * @author Miłosz Gilga
 */
@Component
class CommandEnvironmentBean(
	val environmentBean: EnvironmentBean,
	val i18nBean: I18nBean,
	val jdaColorStoreBean: JdaColorStoreBean,
	val eventQueueBean: EventQueueBean,
	val musicManagersBean: MusicManagersSupplier,
	val distributedAudioClientSupplier: DistributedAudioClientSupplier,
	val jdaInstance: JdaInstance,
	val guildSettingsEventAction: GuildSettingsEventAction,
	val radioStationSupplier: RadioStationSupplier,
	val radioPlaybackMappersCache: RadioPlaybackMappersCache,
	val commandDataSupplier: CommandDataSupplier,
	val commandsCacheBean: CommandsCacheBean,
	val exceptionTrackerHandler: ExceptionTrackerHandler,
	val looselyTransportHandlerBean: LooselyTransportHandlerBean,
)
