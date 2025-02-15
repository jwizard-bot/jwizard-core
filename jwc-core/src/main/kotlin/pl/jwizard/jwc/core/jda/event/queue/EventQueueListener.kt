package pl.jwizard.jwc.core.jda.event.queue

import net.dv8tion.jda.api.events.GenericEvent

interface EventQueueListener<T : GenericEvent> {
	// checks whether the event should be executed
	fun onPredicateExecuteEvent(event: T) = true

	fun onEvent(event: T)

	// triggered when the event times out
	fun onTimeout() {}
}
