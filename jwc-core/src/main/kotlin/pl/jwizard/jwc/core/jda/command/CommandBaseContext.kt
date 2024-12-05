/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

import pl.jwizard.jwl.command.CommandFormatContext

/**
 * Interface representing the base context for a command event.
 *
 * @author Miłosz Gilga
 */
interface CommandBaseContext : CommandFormatContext {

	/**
	 * The language used in the command.
	 */
	val language: String

	/**
	 * Definition of the command on which the event was invoked.
	 */
	val commandName: String

	/**
	 * Determines if notifications from bot responses should be suppressed.
	 */
	val suppressResponseNotifications: Boolean
}
