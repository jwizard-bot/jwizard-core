/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Annotation to mark a class as a Discord command listener for JDA (Java Discord API).
 *
 * This annotation associates the class with a specific command identifier, allowing the command framework to
 * automatically recognize and register the command handler.
 *
 * Usage example:
 *
 * ```kotlin
 * @JdaCommand(id = BotCommand.HELP)
 * class MyJdaCommandListener : CommandBase() {
 *   fun execute(event: CompoundCommandEvent) {
 *     // command body
 *   }
 * }
 * ```
 *
 * @property value The command identifier that this listener will handle.
 * @author Miłosz Gilga
 * @see Command
 * @see CommandBase
 * @see CommandContext
 */
@SingletonComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaCommand(
	val value: Command,
)
