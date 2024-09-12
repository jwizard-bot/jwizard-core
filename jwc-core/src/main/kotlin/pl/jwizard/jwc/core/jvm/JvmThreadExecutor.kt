/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm

import org.springframework.beans.factory.DisposableBean
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Abstract class for executing tasks on a scheduled thread pool with a fixed delay.
 * It uses a [ScheduledThreadPoolExecutor] to manage periodic tasks.
 *
 * @property countOfThreads The number of threads in the pool. Defaults to 1.
 * @constructor Initializes a thread pool executor with the specified number of threads.
 * @author Miłosz Gilga
 */
abstract class JvmThreadExecutor(private val countOfThreads: Int = 1) : DisposableBean, Runnable {

	/**
	 * Executor service for managing the scheduled tasks with fixed delays.
	 * It uses a scheduled thread pool with the specified number of threads defined by [countOfThreads].
	 */
	private val executor = Executors.newScheduledThreadPool(countOfThreads)

	/**
	 * Shuts down the executor service gracefully when the bean is destroyed.
	 * This method is called automatically by Spring when the bean is disposed.
	 */
	override fun destroy() = executor.shutdown()

	/**
	 * Starts the execution of the runnable task with a fixed delay between each execution.
	 *
	 * @param intervalSec The interval in seconds between the end of one execution and the start of the next.
	 */
	fun start(intervalSec: Long) {
		executor.scheduleWithFixedDelay(this, 0, intervalSec, TimeUnit.SECONDS)
	}
}
