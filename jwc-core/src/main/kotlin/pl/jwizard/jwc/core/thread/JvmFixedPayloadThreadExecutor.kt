package pl.jwizard.jwc.core.thread

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class JvmFixedPayloadThreadExecutor<T>(
	countOfThreads: Int = 1,
) : JvmThreadExecutor(countOfThreads) {
	// scheduled events (grab for removal process)
	private val futures = mutableListOf<ScheduledFuture<*>>()

	fun startOnce(delay: Long, unit: TimeUnit, payload: T) {
		futures.add(executor.schedule({ executeJvmThreadWithPayload(payload) }, delay, unit))
	}

	fun cancelQueuedTasks(): Int {
		val futuresSize = futures.size
		futures.forEach { it.cancel(false) }
		futures.clear()
		return futuresSize
	}

	fun getFutureTime(delay: Long, unit: TimeUnit = TimeUnit.SECONDS) =
		LocalDateTime.now().plus(unit.toSeconds(delay), ChronoUnit.SECONDS).toString()

	protected abstract fun executeJvmThreadWithPayload(payload: T)
}
