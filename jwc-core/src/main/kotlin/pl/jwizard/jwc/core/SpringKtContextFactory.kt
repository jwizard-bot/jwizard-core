/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
import pl.jwizard.jwc.core.jvm.JvmDisposable
import pl.jwizard.jwc.core.jvm.JvmDisposableHook
import kotlin.reflect.KClass

/**
 * A kotlin factory class for creating and managing a Spring application context, extending
 * [AnnotationConfigApplicationContext]. It allows initializing the context based on a provided configuration class.
 *
 * @constructor Creates an instance of the Spring context factory using the provided configuration class.
 *
 * @param clazz The configuration class (annotated with [Configuration] or other appropriate annotations) used to
 * 							initialize the Spring context.
 * @author Miłosz Gilga
 */
class SpringKtContextFactory(clazz: KClass<*>) : AnnotationConfigApplicationContext(clazz.java), JvmDisposable {

	companion object {
		private val log = LoggerFactory.getLogger(SpringKtContextFactory::class.java)
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
	 * Retrieves all beans that are annotated with a specific annotation in the Spring context.
	 *
	 * @param T The type of the beans to retrieve.
	 * @param annotation The class of the annotation to filter the beans by.
	 * @return A list of beans that are annotated with the specified annotation.
	 */
	inline fun <reified T : Any> getBeansAnnotatedWith(annotation: KClass<out Annotation>): List<T> =
		getBeansWithAnnotation(annotation.java).map { it.value as T }

	/**
	 * Cleans up the Spring context before JVM shutdown. Invokes the [close] method to release any resources held by
	 * the Spring container.
	 */
	override fun cleanBeforeDisposeJvm() {
		log.info("Shutting down and close internal Spring Context factory...")
		close()
	}
}
