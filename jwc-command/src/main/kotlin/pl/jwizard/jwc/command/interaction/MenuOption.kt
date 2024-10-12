/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

/**
 * Interface representing an option that can be selected from a menu. Implementing classes should provide the necessary
 * details for each menu option.
 *
 * @author Miłosz Gilga
 */
interface MenuOption {

	/**
	 * The unique key representing the menu option. This key is used to identify the option in the selection menu.
	 */
	val key: String

	/**
	 * The value associated with the menu option. This value can be used for processing or storing information related to
	 * the option.
	 */
	val value: String

	/**
	 * The formatted string representation of the option for embedding in messages. This property should return a string
	 * formatted for display in an embed message.
	 */
	val formattedToEmbed: String
}

