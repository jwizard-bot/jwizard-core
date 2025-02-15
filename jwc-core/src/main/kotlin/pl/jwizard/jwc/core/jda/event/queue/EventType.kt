package pl.jwizard.jwc.core.jda.event.queue

import net.dv8tion.jda.api.events.GenericEvent

typealias TEventQueueListener = EventQueueListener<out GenericEvent>

typealias TEventPayload = Pair<MutableSet<TEventQueueListener>, TEventQueueListener>
