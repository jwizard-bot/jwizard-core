/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event

import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Custom annotation used to mark IoC beans as JDA (Java Discord API) event listeners. This annotation is a
 * specialization of the [SingletonComponent] annotation, allowing automatic detection and registration of beans in the
 * IoC context. It is applied at the class level.
 *
 * Usage example:
 *
 * ```kotlin
 * @JdaEventListenerBean
 * class MyJdaEventsListener : ListenerAdapter() {
 *     // Listener methods go here
 * }
 * ```
 *
 * @author Miłosz Gilga
 */
@SingletonComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaEventListenerBean
