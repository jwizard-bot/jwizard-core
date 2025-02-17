package pl.jwizard.jwc.core.jda.event

import org.springframework.stereotype.Component

// put on IoC components extending ListenerAdapter class
@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaEventListener
