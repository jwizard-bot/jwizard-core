/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.handler

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.command.spi.CommandDataSupplier
import pl.jwizard.jwc.command.spi.ModuleDataSupplier
import pl.jwizard.jwc.command.transport.LooselyTransportHandlerBean
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerStore
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean

/**
 * Stored all beans for command event handler.
 *
 * @property commandDataSupplier Supplies command data.
 * @property moduleDataSupplier Supplies module data.
 * @property commandsCacheBean Cache for command execution.
 * @property exceptionTrackerStore Tracks exceptions for reporting.
 * @property i18nBean Provides internationalization support.
 * @property environmentBean Accesses environment-specific properties.
 * @property jdaColorStoreBean Accesses to JDA defined colors for embed messages.
 * @property looselyTransportHandlerBean Handles loosely-typed transport operations between services.
 * @author Miłosz Gilga
 * @see CommandEventHandler
 */
@Component
class CommandEventHandlerEnvironmentBean(
	val commandDataSupplier: CommandDataSupplier,
	val moduleDataSupplier: ModuleDataSupplier,
	val commandsCacheBean: CommandsCacheBean,
	val exceptionTrackerStore: ExceptionTrackerStore,
	val i18nBean: I18nBean,
	val environmentBean: EnvironmentBean,
	val jdaColorStoreBean: JdaColorStoreBean,
	val looselyTransportHandlerBean: LooselyTransportHandlerBean,
)
