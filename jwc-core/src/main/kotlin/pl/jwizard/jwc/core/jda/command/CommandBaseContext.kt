package pl.jwizard.jwc.core.jda.command

import pl.jwizard.jwl.command.CommandFormatContext

interface CommandBaseContext : CommandFormatContext {
	val language: String

	val commandName: String

	// send message without notifications (works only for non-interaction messages)
	val suppressResponseNotifications: Boolean
}
