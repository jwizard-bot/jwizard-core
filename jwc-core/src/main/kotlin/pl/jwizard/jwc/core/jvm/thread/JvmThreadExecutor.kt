/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm.thread

import pl.jwizard.jwl.ioc.CleanupAfterIoCDestroy
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

/**
 * Abstract base class for managing a thread pool executor with a fixed number of threads. It implements IoC
 * [CleanupAfterIoCDestroy] interface to allow proper resource cleanup.
 *
 * @property countOfThreads The number of threads in the thread pool.
 * @author Miłosz Gilga
 */
abstract class JvmThreadExecutor(private val countOfThreads: Int = 1) : CleanupAfterIoCDestroy {

	/**
	 * The scheduled executor service managing the thread pool. Tasks can be scheduled with a fixed delay or executed
	 * periodically.
	 */
	protected val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(countOfThreads)

	/**
	 * Shuts down the thread pool, stopping all scheduled tasks and preventing new ones from being accepted. This is
	 * invoked automatically when the bean is destroyed in a IoC-managed context.
	 */
	override fun destroy() = executor.shutdown()
}
