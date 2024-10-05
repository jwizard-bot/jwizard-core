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
fun formatQualifier(name: String, id: Long) = "\"%s <@%s>\"".format(name, id)

/**
 * Creates a Markdown link.
 *
 * This function generates a markdown-formatted link using the provided name and link.
 *
 * @param name The text that will be displayed for the link.
 * @param link The URL to which the link points. Can be null, in which case the link will be created without a URL.
 * @return A string representing a Markdown link in the format "[name](link)".
 * @author Miłosz Gilga
 */
fun mdLink(name: String, link: String?) = "[$name]($link)"

/**
 * Formats a key-value pair as a string.
 *
 * This function takes a key and a value, and formats them into a string representation.  If the value is null, it will
 * still return the key followed by a colon.
 *
 * @param key The key to be displayed.
 * @param value The value associated with the key. Can be null.
 * @return A formatted string in the form of "key: value". If value is null, the result will be "key: null".
 * @author Miłosz Gilga
 */
fun keyValueFormat(key: String, value: Any?) = "$key: $value"
