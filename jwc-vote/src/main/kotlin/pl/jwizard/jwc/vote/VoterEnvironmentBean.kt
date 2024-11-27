/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote

import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * A configuration bean that encapsulates dependencies required for the voting process.
 *
 * @property environment Provides access to application-level environment properties, including configuration settings.
 * @property i18n Handles internationalization, offering support for localized messages during the voting process.
 * @property jdaColorStore Manages predefined color schemes used for JDA (Java Discord API) embeds and components.
 * @property eventQueue Manages the event queue, ensuring events are processed asynchronously and efficiently.
 * @property looselyTransportHandler A handler for managing loosely coupled transport operations (sending messages).
 */
@SingletonComponent
class VoterEnvironmentBean(
	val environment: EnvironmentBean,
	val i18n: I18nBean,
	val jdaColorStore: JdaColorsCacheBean,
	val eventQueue: EventQueueBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
)
