/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

/**
 * Interface representing the base context for a command event.
 *
 * @author Miłosz Gilga
 */
interface CommandBaseContext {

	/**
	 * The language used in the command.
	 */
	val language: String

	/**
	 * Definition of the command on which the event was invoked.
	 */
	val commandName: String

	/**
	 * The command prefix used for executing commands in this guild.
	 */
	val prefix: String

	/**
	 * Determines if notifications from bot responses should be suppressed.
	 */
	val suppressResponseNotifications: Boolean

	/**
	 * A boolean indicating whether the command is executed as a slash command event.
	 */
	val isSlashEvent: Boolean
}
