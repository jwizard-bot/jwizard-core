/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
import pl.jwizard.jwc.core.jvm.JvmDisposable
import pl.jwizard.jwc.core.jvm.JvmDisposableHook
import pl.jwizard.jwc.core.util.logger
import kotlin.reflect.KClass

/**
 * A kotlin factory class for creating and managing a Spring application context, extending
 * [AnnotationConfigApplicationContext]. It allows initializing the context based on a provided configuration class.
 *
 * @constructor Creates an instance of the Spring context factory using the provided configuration class.
 *
 * @param clazz The configuration class (annotated with [Configuration] or other appropriate annotations) used to
 *        initialize the Spring context.
 * @author Miłosz Gilga
 */
class SpringKtContextFactory(clazz: KClass<*>) : AnnotationConfigApplicationContext(clazz.java), JvmDisposable {

	companion object {
		private val log = logger<SpringKtContextFactory>()
	}

	/**
	 * Hook to handle JVM shutdown by cleaning up the Spring context properly. Uses [JvmDisposableHook] to add the
	 * shutdown hook to the JVM.
	 */
	private val jvmDisposableHook = JvmDisposableHook(this)

	init {
		jvmDisposableHook.initHook()
	}

	/**
	 * Retrieves a registered Spring bean of the specified type from the application context.
	 *
	 * @param T The type of the requested bean.
	 * @param beanType The class type of the bean to be retrieved.
	 * @return An instance of the bean of the specified type.
	 */
	fun <T : Any> getBean(beanType: KClass<T>): T = super.getBean(beanType.java)

	/**
	 * Retrieves all Spring beans of type [T] that are annotated with the annotation [U]. The result is a list of beans
	 * that are both of the specified type [T] and annotated with [U].
	 *
	 * @param T The type of beans to retrieve from the Spring context.
	 * @param U The annotation type that must be present on the beans to be included in the result.
	 * @return A list of beans of type [T] that are annotated with [U].
	 */
	inline fun <reified T : Any, reified U : Annotation> getBeansAnnotatedWith(): List<T> =
		getBeansWithAnnotation(U::class.java).map { it.value as T }

	/**
	 * Cleans up the Spring context before JVM shutdown. Invokes the [close] method to release any resources held by
	 * the Spring container.
	 */
	override fun cleanBeforeDisposeJvm() {
		log.info("Shutting down and close internal Spring Context factory...")
		close()
	}
}
