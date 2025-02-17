package pl.jwizard.jwc.command.reflect

import org.springframework.stereotype.Component
import pl.jwizard.jwl.command.Command

// Use this annotation at every JDA command listener
@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaCommand(
	val value: Command,
)
