/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.exception

/**
 * Exception thrown when there is an error parsing a command.
 *
 * This exception indicates that the command input could not be correctly interpreted, potentially due to invalid
 * syntax, missing arguments, or other issues that prevent successful command execution.
 *
 * @author Miłosz Gilga
 */
class CommandParserException : RuntimeException()
