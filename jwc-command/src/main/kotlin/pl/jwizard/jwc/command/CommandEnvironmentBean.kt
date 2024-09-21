/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * A Spring component that aggregates various environment-related beans used in the command processing system.
 *
 * @property environmentBean Provides access to environment-related properties and configurations.
 * @property i18nBean Manages internationalization (i18n) settings and localized messages.
 * @property jdaColorStoreBean Handles color settings for the JDA (Java Discord API) interaction.
 * @property eventQueueBean Manages the event queue, enabling processing of JDA events in the application.
 * @author Miłosz Gilga
 */
@Component
class CommandEnvironmentBean(
	val environmentBean: EnvironmentBean,
	val i18nBean: I18nBean,
	val jdaColorStoreBean: JdaColorStoreBean,
	val eventQueueBean: EventQueueBean,
)
