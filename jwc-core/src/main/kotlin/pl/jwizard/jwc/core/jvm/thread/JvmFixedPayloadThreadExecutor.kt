/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm.thread

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
	 * Schedules a single execution of a task with the provided payload after a specified delay.
	 *
	 * @param delay The time delay before the task is executed.
	 * @param unit The time unit of the delay (e.g., seconds, milliseconds).
	 * @param payload The payload to be processed by the thread.
	 */
	fun startOnce(delay: Long, unit: TimeUnit, payload: T) {
		executor.schedule({ executeJvmThreadWithPayload(payload) }, delay, unit)
	}

	/**
	 * Executes the task with the provided payload. This method should be implemented by subclasses to define the
	 * specific behavior of how the payload is handled.
	 *
	 * @param payload The payload to be processed by the thread.
	 */
	protected abstract fun executeJvmThreadWithPayload(payload: T)
}
