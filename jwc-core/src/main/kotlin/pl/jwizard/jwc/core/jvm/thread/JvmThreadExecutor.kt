package pl.jwizard.jwc.core.jvm.thread

import pl.jwizard.jwl.ioc.CleanupAfterIoCDestroy
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

abstract class JvmThreadExecutor(countOfThreads: Int = 1) : CleanupAfterIoCDestroy {
	protected val executor: ScheduledExecutorService = Executors
		.newScheduledThreadPool(countOfThreads)

	override fun destroy() = executor.shutdown()
}
