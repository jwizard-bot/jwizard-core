/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core

import org.springframework.context.annotation.ComponentScan

/**
 * Apply this annotation on main class, where you invoke `run` static method from [DiscordBotAppRunner] class.
 *
 * @author Miłosz Gilga
 */
@ComponentScan(basePackages = [DiscordBotAppRunner.BASE_PACKAGE])
@Retention(AnnotationRetention.RUNTIME)
annotation class DiscordBotApp
