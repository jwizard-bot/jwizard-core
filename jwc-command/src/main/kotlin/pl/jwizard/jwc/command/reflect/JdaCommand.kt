/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

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
 * class MyCommandHandler : CommandHandler {
 *   fun execute(context: CommandContext, response: TFutureResponse) {
 *     // command body
 *     response.complete(/* embed response */)
 *   }
 * }
 * ```
 *
 * @property value The command identifier that this listener will handle.
 * @author Miłosz Gilga
 */
@SingletonComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaCommand(
	val value: Command,
)
