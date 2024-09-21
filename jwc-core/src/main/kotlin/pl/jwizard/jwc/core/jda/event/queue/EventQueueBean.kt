/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event.queue

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.EventListener
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.jvm.thread.JvmFixedPayloadThreadExecutor
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

/**
 * Class responsible for managing scheduled events and listeners in a multithreaded environment. It listens for
 * specific [GenericEvent] types and executes associated event listeners after a defined timeout or when specific
 * conditions are met.
 *
 * This class stores declared JDA events in a map and triggers them upon receiving a signal from the JDA event system.
 * If no specific time is provided during initialization, after which the event should be removed, it will be removed
 * according to the maximum JDA interaction duration. If a time greater than the maximum interaction duration is
 * specified when creating the event, the duration will be automatically replaced with the maximum interaction time.
 *
 * @property environmentBean Provides access to environment properties such as interaction timeout.
 * @author Miłosz Gilga
 * @see JvmFixedPayloadThreadExecutor
 * @see EventListener
 * @see TEventPayload
 */
@JdaEventListenerBean
class EventQueueBean(
	private val environmentBean: EnvironmentBean
) : JvmFixedPayloadThreadExecutor<TEventPayload>(THREADS_COUNT), EventListener {

	companion object {
		/**
		 * Number of threads to handle event payloads.
		 */
		private const val THREADS_COUNT = 5

		/**
		 * Name of the method that checks if the event should be executed.
		 */
		private const val PREDICATE_METHOD_NAME = "PredicateExecuteEvent"

		/**
		 * Name of the method that executes the event.
		 */
		private const val EXECUTE_METHOD_NAME = "Event"
	}

	/**
	 * Map storing event types and their associated listeners. Each event type is mapped to a set of listeners waiting
	 * for that event.
	 */
	private val waitingEvents = ConcurrentHashMap<KClass<*>, MutableSet<TEventQueueListener>>()

	/**
	 * Maximum time (in seconds) before an event listener is considered inactive. Retrieved from environment properties.
	 */
	private val timeToDelete =
		environmentBean.getProperty<Long>(BotProperty.JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC)

	/**
	 * Lookup for dynamically accessing methods related to event execution.
	 */
	private val lookup = MethodHandles.lookup()

	/**
	 * Waits for a scheduled event to occur within a specified time frame.
	 *
	 * If the waiting time exceeds the allowed [timeToDelete] value, the time is capped. Adds the event listener to
	 * the queue and starts a timer for its execution.
	 *
	 * @param T The type of the event being waited for, must extend [GenericEvent].
	 * @param clazz The class type of the event.
	 * @param eventQueueListener Listener that will handle the event when it occurs.
	 * @param waitingTime Time to wait for the event.
	 * @param timeUnit Unit of time (ex. seconds, minutes) for the [waitingTime].
	 */
	fun <T : GenericEvent> waitForScheduledEvent(
		clazz: KClass<T>,
		eventQueueListener: EventQueueListener<T>,
		waitingTime: Long,
		timeUnit: TimeUnit
	) {
		val waitingTimeSec = timeUnit.toSeconds(waitingTime)
		val (calcWaitingTime, calcTimeUnits) = if (waitingTimeSec > timeToDelete) {
			Pair(timeToDelete, TimeUnit.SECONDS)
		} else {
			Pair(waitingTime, timeUnit)
		}
		val waitingEventsContainer = addToQueue(clazz, eventQueueListener)
		startOnce(calcWaitingTime, calcTimeUnits, Pair(waitingEventsContainer, eventQueueListener))
	}

	/**
	 * Waits for an event without scheduling a custom timeout. The default waiting time is defined by [timeToDelete]
	 * from environment properties.
	 *
	 * @param T The type of the event being waited for, must extend [GenericEvent].
	 * @param clazz The class type of the event.
	 * @param eventQueueListener Listener that will handle the event when it occurs.
	 */
	fun <T : GenericEvent> waitForEvent(clazz: KClass<T>, eventQueueListener: EventQueueListener<T>) {
		waitForScheduledEvent(clazz, eventQueueListener, timeToDelete, TimeUnit.SECONDS)
	}

	/**
	 * Handles the occurrence of an event.
	 *
	 * This method is triggered by the JDA event system. It checks if any listeners are registered for the event type
	 * and executes the listeners if their conditions are met.
	 *
	 * @param event The event that occurred.
	 */
	override fun onEvent(event: GenericEvent) {
		var clazz: Class<*>? = event::class.java
		while (clazz != null) {
			val set = waitingEvents[clazz.kotlin]
			if (set != null) {
				val elements = set.toTypedArray().filter { executeEvent(event, it) }.toSet()
				set.removeAll(elements)
			}
			if (event is ShutdownEvent) {
				destroy()
			} else {
				clazz = clazz.superclass
			}
		}
	}

	/**
	 * Executes a payload in a separate thread. Removes the listener from the container when the timeout is reached and
	 * triggers the `onTimeout` callback for the listener.
	 *
	 * @param payload The payload containing the event and listener information.
	 */
	override fun executeJvmThreadWithPayload(payload: TEventPayload) {
		val (container, listener) = payload
		if (container.remove(listener)) {
			listener.onTimeout()
		}
	}

	/**
	 * Executes the event for a given listener if the listener's condition is met. The method checks the listener's
	 * predicate method to determine if the event should be executed, and if true, invokes the listener's event
	 * handling method.
	 *
	 * @param event The event that occurred.
	 * @param listener The listener waiting for the event.
	 * @return `true` if the event was successfully executed, `false` otherwise.
	 */
	private fun executeEvent(event: GenericEvent, listener: TEventQueueListener): Boolean {
		val predicate = findMethod(PREDICATE_METHOD_NAME, Boolean::class.java)
		val execute = findMethod(EXECUTE_METHOD_NAME, Void.TYPE)
		val predicateResult = predicate.invoke(listener, event) as Boolean
		if (predicateResult) {
			execute.invoke(listener, event)
		}
		return predicateResult
	}

	/**
	 * Dynamically finds a method by name and return type from the [EventQueueListener] class. The method is used to
	 * locate the listener's predicate and execution methods.
	 *
	 * @param name Name of the method to find.
	 * @param returnType The return type of the method.
	 * @return The [MethodHandle] representing the found method.
	 */
	private fun findMethod(name: String, returnType: Class<*>) = lookup.findVirtual(
		EventQueueListener::class.java, "on$name",
		MethodType.methodType(returnType, GenericEvent::class.java)
	)

	/**
	 * Adds an event listener to the queue for a given event type. If the event type is not already in the queue, it
	 * initializes a new entry for it.
	 *
	 * @param T The type of the event being added to the queue, must extend [GenericEvent].
	 * @param clazz The class type of the event.
	 * @param eventQueueListener The listener that will handle the event.
	 * @return A mutable set of listeners for the event type.
	 */
	private fun <T : GenericEvent> addToQueue(
		clazz: KClass<T>,
		eventQueueListener: EventQueueListener<T>
	): MutableSet<TEventQueueListener> {
		val waitingEventsContainer = waitingEvents.computeIfAbsent(clazz) { mutableSetOf() }
		waitingEventsContainer.add(eventQueueListener)
		return waitingEventsContainer
	}
}
