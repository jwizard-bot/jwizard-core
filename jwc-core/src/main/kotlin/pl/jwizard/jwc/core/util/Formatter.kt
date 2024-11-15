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
 * @param name The text that will be displayed for the link. Can be null.
 * @param link The URL to which the link points. Can be null, in which case the link will be created without a URL.
 * @return A string representing a Markdown link in the format "[name](link)".
 * @author Miłosz Gilga
 */
fun mdLink(name: Any?, link: String?) = "[$name]($link)"

/**
 * Formats the given text as bold Markdown.
 *
 * This function wraps the input text with double asterisks (`**`), which is the Markdown syntax for bold text.
 *
 * @param text The string to be formatted as bold.
 * @return A new string that contains the input text formatted as bold in Markdown.
 * @author Miłosz Gilga
 */
fun mdBold(text: String) = "**$text**"

/**
 * Wraps the provided text in Markdown inline code formatting (using backticks) for embedding it in Discord messages.
 * If the text is null, it will wrap `null` instead.
 *
 * @param text The text to be wrapped in inline code formatting.
 * @return The text wrapped in backticks, suitable for Discord Markdown formatting.
 * @author Miłosz Gilga
 */
fun mdCode(text: String?) = "`$text`"

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
