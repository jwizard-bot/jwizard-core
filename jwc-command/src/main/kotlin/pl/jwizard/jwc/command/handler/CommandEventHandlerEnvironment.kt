package pl.jwizard.jwc.command.handler

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandsCache
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandler
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwc.core.property.GuildEnvironment
import pl.jwizard.jwc.exception.ExceptionTrackerHandler
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.property.BaseEnvironment

@Component
internal class CommandEventHandlerEnvironment(
	val commandDataSupplier: CommandDataSupplier,
	val moduleDataSupplier: ModuleDataSupplier,
	val commandsCache: CommandsCache,
	val exceptionTrackerHandler: ExceptionTrackerHandler,
	val i18n: I18n,
	val environment: BaseEnvironment,
	val guildEnvironment: GuildEnvironment,
	val jdaColorStore: JdaColorsCache,
	val looselyTransportHandler: LooselyTransportHandler,
)
