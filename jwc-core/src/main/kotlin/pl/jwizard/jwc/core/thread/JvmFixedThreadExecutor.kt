package pl.jwizard.jwc.core.thread

import java.util.concurrent.TimeUnit

abstract class JvmFixedThreadExecutor(
	countOfThreads: Int = 1,
) : JvmThreadExecutor(countOfThreads), Runnable {
	fun start(intervalSec: Long) {
		executor.scheduleWithFixedDelay(this, 0, intervalSec, TimeUnit.SECONDS)
	}

	final override fun run() = executeJvmThread()

	protected abstract fun executeJvmThread()
}
