package pl.jwizard.jwc.command.reflect

import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

// Use this annotation at every JDA command listener
@SingletonComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaCommand(
	val value: Command,
)
