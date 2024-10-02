/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm.thread

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Abstract class for executing tasks on a scheduled thread pool with a fixed delay.
 * It uses a [ScheduledThreadPoolExecutor] to manage periodic tasks.
 *
 * @property countOfThreads The number of threads in the pool. Defaults to 1.
 * @author Miłosz Gilga
 */
abstract class JvmFixedThreadExecutor(
	private val countOfThreads: Int = 1,
) : JvmThreadExecutor(countOfThreads), Runnable {

	/**
	 * Starts the execution of the runnable task with a fixed delay between each execution.
	 *
	 * @param intervalSec The interval in seconds between the end of one execution and the start of the next.
	 */
	fun start(intervalSec: Long) {
		executor.scheduleWithFixedDelay(this, 0, intervalSec, TimeUnit.SECONDS)
	}

	/**
	 * Umbrella method for better code readability and make possibility to implement [Runnable] interface in inherit
	 * classes. Perform per-thread job.
	 *
	 * **Do not implement `run` method in subclasses. For execute job in thread, use [executeJvmThread].**
	 */
	final override fun run() = executeJvmThread()

	/**
	 * Code in this method will be executed per thread in declared thread pool with size defined in [countOfThreads]
	 * variable.
	 */
	protected abstract fun executeJvmThread()
}
