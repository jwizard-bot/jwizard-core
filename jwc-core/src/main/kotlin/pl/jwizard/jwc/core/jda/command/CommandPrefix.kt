/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

/**
 * Interface for parsing a command with its corresponding prefix. This helps in processing and identifying commands
 * executed in the application.
 *
 * @author Miłosz Gilga
 */
interface CommandPrefix {

	/**
	 * Parses the command by combining it with the relevant prefix from the given context.
	 *
	 * @param context the context containing information about the current command and its execution environment.
	 * @return the full command string with its prefix.
	 */
	fun parseWithPrefix(context: CommandBaseContext): String
}
