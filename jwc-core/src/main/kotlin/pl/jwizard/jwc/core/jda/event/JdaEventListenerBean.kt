/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event

import org.springframework.stereotype.Component

/**
 * Custom annotation used to mark Spring beans as JDA (Java Discord API) event listeners. This annotation is a
 * specialization of the [Component] annotation, allowing automatic detection and registration of beans in the
 * Spring context. It is applied at the class level.
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
@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaEventListenerBean
