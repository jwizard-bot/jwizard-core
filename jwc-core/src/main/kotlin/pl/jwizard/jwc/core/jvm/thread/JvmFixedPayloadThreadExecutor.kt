/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm.thread

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Abstract class representing a thread executor with a fixed number of threads. It provides functionality to schedule
 * tasks with a specific payload and delay.
 *
 * @param T The type of the payload to be processed by the thread. By default, single thread.
 * @property countOfThreads The fixed number of threads in the thread pool.
 * @author Miłosz Gilga
 */
abstract class JvmFixedPayloadThreadExecutor<T>(
	private val countOfThreads: Int = 1
) : JvmThreadExecutor(countOfThreads) {

	/**
	 * Future representing the scheduled task.
	 */
	private var future: ScheduledFuture<*>? = null

	/**
	 * Schedules a single execution of a task with the provided payload after a specified delay.
	 *
	 * @param delay The time delay before the task is executed.
	 * @param unit The time unit of the delay (e.g., seconds, milliseconds).
	 * @param payload The payload to be processed by the thread.
	 */
	fun startOnce(delay: Long, unit: TimeUnit, payload: T) {
		future = executor.schedule({ executeJvmThreadWithPayload(payload) }, delay, unit)
	}

	/**
	 * Cancels the scheduled task if it has not yet executed.
	 */
	fun gracefullyShutdown() = future?.cancel(false)

	/**
	 * Returns the future execution time based on the given delay.
	 *
	 * @param delay The delay for the future time.
	 * @param unit Time unit of the delay (default: seconds).
	 * @return Future time as a string.
	 */
	fun getFutureTime(delay: Long, unit: TimeUnit = TimeUnit.SECONDS) =
		LocalDateTime.now().plus(unit.toSeconds(delay), ChronoUnit.SECONDS).toString()

	/**
	 * Executes the task with the provided payload. This method should be implemented by subclasses to define the
	 * specific behavior of how the payload is handled.
	 *
	 * @param payload The payload to be processed by the thread.
	 */
	protected abstract fun executeJvmThreadWithPayload(payload: T)
}
