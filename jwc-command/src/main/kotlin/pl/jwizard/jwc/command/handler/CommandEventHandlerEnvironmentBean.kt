/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.exception.ExceptionTrackerHandlerBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Stored all beans for command event handler.
 *
 * @property commandDataSupplier Supplies command data.
 * @property moduleDataSupplier Supplies module data.
 * @property commandsCache Cache for command execution.
 * @property exceptionTrackerHandler Tracks exceptions for reporting.
 * @property i18n Provides internationalization support.
 * @property environment Accesses environment-specific properties.
 * @property jdaColorStore Accesses to JDA defined colors for embed messages.
 * @property looselyTransportHandler Handles loosely-typed transport operations between services.
 * @author Miłosz Gilga
 * @see CommandEventHandler
 */
@SingletonComponent
class CommandEventHandlerEnvironmentBean(
	val commandDataSupplier: CommandDataSupplier,
	val moduleDataSupplier: ModuleDataSupplier,
	val commandsCache: CommandsCacheBean,
	val exceptionTrackerHandler: ExceptionTrackerHandlerBean,
	val i18n: I18nBean,
	val environment: EnvironmentBean,
	val jdaColorStore: JdaColorStoreBean,
	val looselyTransportHandler: LooselyTransportHandlerBean,
)
