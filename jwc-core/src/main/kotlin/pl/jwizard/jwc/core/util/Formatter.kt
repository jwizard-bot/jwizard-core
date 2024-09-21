/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

/**
 * Formats a string to represent a qualifier in Discord.
 *
 * This function takes a name and an ID, and formats them into a string that follows the Discord mention format.
 *
 * @param name Readable name.
 * @param id The numbered unique ID.
 * @return A formatted string in the form of "name <@id>".
 * @author Miłosz Gilga
 */
fun formatQualifier(name: String, id: String) = "\"%s <@%s>\"".format(name, id)
