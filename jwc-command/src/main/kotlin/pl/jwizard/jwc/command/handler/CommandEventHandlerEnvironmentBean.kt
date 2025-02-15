package pl.jwizard.jwc.command.handler

import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
class CommandEventHandlerEnvironmentBean(
	val commandDataSupplier: CommandDataSupplier,
	val moduleDataSupplier: ModuleDataSupplier,
	val commandsCache: CommandsCacheBean,
	val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
	val i18n: I18nBean,
	val environment: EnvironmentBean,
	val jdaColorStore: JdaColorsCacheBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
)
