/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Lazy
@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandListenerBean(
	val id: String
)
