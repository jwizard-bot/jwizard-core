/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jvm

/**
 * Interface defining a method to perform cleanup before JVM shutdown.
 *
 * Implementations of this method should ensure that necessary cleanup actions are performed before the JVM shuts down.
 *
 * @author Miłosz Gilga
 */
interface JvmDisposable {

	/**
	 * Method to be invoked before the JVM shuts down. Allows for performing cleanup operations such as resource
	 * de-allocation or closing open connections.
	 */
	fun cleanBeforeDisposeJvm()
}
