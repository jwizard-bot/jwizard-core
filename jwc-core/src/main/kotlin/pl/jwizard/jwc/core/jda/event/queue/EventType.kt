/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event.queue

import net.dv8tion.jda.api.events.GenericEvent

/**
 * Type alias representing a listener for any subtype of [GenericEvent]. This allows listeners to be easily referenced
 * when handling various event types.
 *
 * @author Miłosz Gilga
 */
typealias TEventQueueListener = EventQueueListener<out GenericEvent>

/**
 * Type alias representing the payload for event processing. The payload is a pair consisting of a set of listeners
 * waiting for the event and a single listener that is currently processing the event.
 *
 * - The first element (`MutableSet<TEventQueueListener>`) is a set of listeners associated with the event.
 * - The second element (`TEventQueueListener`) represents the specific listener being processed.
 *
 * @author Miłosz Gilga
 */
typealias TEventPayload = Pair<MutableSet<TEventQueueListener>, TEventQueueListener>
