package pl.jwizard.jwc.core.thread

import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

abstract class JvmThreadExecutor(countOfThreads: Int = 1) : DisposableBean {
	protected val executor: ScheduledExecutorService = Executors
		.newScheduledThreadPool(countOfThreads)

	override fun destroy() = executor.shutdown()
}
