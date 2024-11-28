/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event.queue

import net.dv8tion.jda.api.events.GenericEvent

/**
 * Interface representing an event listener in the event queue system. Listeners are triggered when specific JDA events
 * occur or a timeout is reached. The listener can define custom conditions under which the event should be executed.
 *
 * @param T The type of the event the listener is waiting for, must extend [GenericEvent].
 * @author Miłosz Gilga
 */
interface EventQueueListener<T : GenericEvent> {

	/**
	 * Predicate that checks whether the event should be executed. By default, it always returns `true`, meaning the
	 * event will be executed. This method can be overridden to provide custom logic to decide if the event should
	 * proceed.
	 *
	 * @param event The event that occurred.
	 * @return `true` if the event should be executed, `false` otherwise.
	 */
	fun onPredicateExecuteEvent(event: T) = true

	/**
	 * Method to handle the event when it occurs. This method is called if the predicate returns `true`, indicating
	 * that the event should be executed.
	 *
	 * @param event The event that occurred.
	 */
	fun onEvent(event: T)

	/**
	 * Method that is triggered when the event times out. By default, it does nothing, but it can be overridden to
	 * handle timeouts.
	 */
	fun onTimeout() {}
}
