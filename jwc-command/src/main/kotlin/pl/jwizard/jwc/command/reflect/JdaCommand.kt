/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.reflect

import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.refer.Command

/**
 * Annotation to mark a class as a Discord command listener for JDA (Java Discord API).
 *
 * This annotation associates the
 * class with a specific command identifier, allowing the command framework to automatically recognize and register the
 * command handler.
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
 * @property id The command identifier that this listener will handle.
 * @author Miłosz Gilga
 * @see Command
 * @see CommandBase
 * @see CommandContext
 */
@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class JdaCommand(
	val id: Command,
)
