package pl.jwizard.jwc.vote

import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
class VoterEnvironmentBean(
	val environment: EnvironmentBean,
	val i18n: I18nBean,
	val jdaColorStore: JdaColorsCacheBean,
	val eventQueue: EventQueueBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
)
