/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
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
class SpringKtContextFactory(clazz: KClass<*>) : AnnotationConfigApplicationContext(clazz.java) {

	/**
	 * Retrieves a registered Spring bean of the specified type from the application context.
	 *
	 * @param T The type of the requested bean.
	 * @param beanType The class type of the bean to be retrieved.
	 * @return An instance of the bean of the specified type.
	 */
	fun <T : Any> getBean(beanType: KClass<T>): T = super.getBean(beanType.java)
}
