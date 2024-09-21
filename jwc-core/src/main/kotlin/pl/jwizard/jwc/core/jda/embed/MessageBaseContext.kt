/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.embed

/**
 * Interface representing the context for message embeds.
 *
 * This context provides necessary information to customize message embeds such as author details and the language
 * for the guild.
 *
 * @author Miłosz Gilga
 */
interface MessageBaseContext {

	/**
	 * The URL of the author's avatar. This is used for displaying the author's image in the embed.
	 */
	val authorAvatarUrl: String

	/**
	 * The name of the author. This is used to display the author's name in the embed.
	 */
	val authorName: String

	/**
	 * The language used in the guild. This helps in localizing the content of the embed based on the guild's language
	 * preference.
	 */
	val guildLanguage: String
}
