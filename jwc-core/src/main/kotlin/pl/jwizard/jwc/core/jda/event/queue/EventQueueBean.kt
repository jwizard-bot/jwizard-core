package pl.jwizard.jwc.core.jda.event.queue

import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.EventListener
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.jvm.thread.JvmFixedPayloadThreadExecutor
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@JdaEventListenerBean
class EventQueueBean(
	environment: EnvironmentBean,
) : JvmFixedPayloadThreadExecutor<TEventPayload>(countOfThreads = 5), EventListener {
	companion object {
		// name of the method that checks if the event should be executed
		private const val PREDICATE_METHOD_NAME = "PredicateExecuteEvent"

		// name of the method that executes the event
		private const val EXECUTE_METHOD_NAME = "Event"
	}

	private val lookup = MethodHandles.lookup()

	// event types (keys) and associated listeners (values)
	// each event type is mapped to a set of listeners waiting for that event
	private val waitingEvents = ConcurrentHashMap<KClass<*>, MutableSet<TEventQueueListener>>()

	// max time before an event listener is considered inactive
	private val timeToDelete =
		environment.getProperty<Long>(BotProperty.JDA_INTERACTION_MESSAGE_COMPONENT_DISABLE_DELAY_SEC)

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

	fun <T : GenericEvent> waitForEvent(clazz: KClass<T>, eventQueueListener: EventQueueListener<T>) {
		waitForScheduledEvent(clazz, eventQueueListener, timeToDelete, TimeUnit.SECONDS)
	}

	override fun onEvent(event: GenericEvent) {
		var clazz: Class<*>? = event::class.java
		// iterate through the class hierarchy until reaching the top-level class (null superclass)
		while (clazz != null) {
			// retrieve a set of event handlers waiting for this event type
			val set = waitingEvents[clazz.kotlin]
			if (set != null) {
				val elements = set.toTypedArray().filter { executeEvent(event, it) }.toSet()
				set.removeAll(elements)
			}
			// if the event is a ShutdownEvent, clean up resources
			if (event is ShutdownEvent) {
				destroy()
			} else {
				clazz = clazz.superclass
			}
		}
	}

	override fun executeJvmThreadWithPayload(payload: TEventPayload) {
		val (container, listener) = payload
		if (container.remove(listener)) {
			listener.onTimeout()
		}
	}

	private fun executeEvent(event: GenericEvent, listener: TEventQueueListener): Boolean {
		val predicate = findMethod(PREDICATE_METHOD_NAME, Boolean::class.java)
		val execute = findMethod(EXECUTE_METHOD_NAME, Void.TYPE)
		val predicateResult = predicate.invoke(listener, event) as Boolean
		if (predicateResult) {
			execute.invoke(listener, event)
		}
		return predicateResult
	}

	private fun findMethod(name: String, returnType: Class<*>) = lookup.findVirtual(
		EventQueueListener::class.java, "on$name",
		MethodType.methodType(returnType, GenericEvent::class.java)
	)

	private fun <T : GenericEvent> addToQueue(
		clazz: KClass<T>,
		eventQueueListener: EventQueueListener<T>,
	): MutableSet<TEventQueueListener> {
		val waitingEventsContainer = waitingEvents.computeIfAbsent(clazz) { mutableSetOf() }
		waitingEventsContainer.add(eventQueueListener)
		return waitingEventsContainer
	}
}
