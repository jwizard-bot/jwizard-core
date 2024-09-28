/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm

import org.slf4j.LoggerFactory
import kotlin.reflect.jvm.jvmName

/**
 * Class responsible for adding a shutdown hook to the JVM that triggers a user-defined cleanup method before the
 * JVM shuts down.
 *
 * @property jvmDisposable Instance of a class implementing the [JvmDisposable] interface, which will be used to
 *           perform cleanup operations.
 * @author Miłosz Gilga
 */
class JvmDisposableHook(private val jvmDisposable: JvmDisposable) {

	companion object {
		private val log = LoggerFactory.getLogger(JvmDisposableHook::class.java)
	}

	/**
	 * Instance of the [Runtime] class, which manages JVM events, including adding shutdown hooks.
	 */
	private val runtime = Runtime.getRuntime()

	/**
	 * Adds a shutdown hook to the JVM that invokes the [JvmDisposable.cleanBeforeDisposeJvm] method before the JVM
	 * shuts down.
	 */
	fun initHook() {
		val thread = Thread { jvmDisposable.cleanBeforeDisposeJvm() }
		runtime.addShutdownHook(thread)
		log.info("Init JVM disposable hook for: {}.", jvmDisposable::class.jvmName)
	}
}
