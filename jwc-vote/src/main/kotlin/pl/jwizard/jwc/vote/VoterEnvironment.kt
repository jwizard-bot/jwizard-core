package pl.jwizard.jwc.vote

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.transport.LooselyTransportHandler
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwc.core.jda.event.queue.EventQueue
import pl.jwizard.jwc.core.property.GuildEnvironment
import pl.jwizard.jwl.i18n.I18n

@Component
class VoterEnvironment(
	val guildEnvironment: GuildEnvironment,
	val i18n: I18n,
	val jdaColorStore: JdaColorsCache,
	val eventQueue: EventQueue,
	val looselyTransportHandler: LooselyTransportHandler,
)
