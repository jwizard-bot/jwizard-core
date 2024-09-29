/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow

/**
 * Represents the response from a command execution, including any embedded messages and action rows to be sent in the
 * Discord channel.
 *
 * @property embedMessages A list of embedded messages to be included in the response.
 * @property actionRows A list of action rows containing interactive components.
 * @property privateMessage Indicates whether the response should be sent as a private message.
 * @property privateMessageUserId The ID of the user to whom the private message should be sent, if applicable.
 * @author Miłosz Gilga
 */
data class CommandResponse(
	val embedMessages: List<MessageEmbed>,
	val actionRows: List<ActionRow>,
	val privateMessage: Boolean,
	val privateMessageUserId: Long?,
) {

	companion object {
		/**
		 * Creates a [CommandResponse] for a public interaction message.
		 *
		 * This method constructs a response intended for a public Discord channel, including the provided embed message
		 * and action row.
		 *
		 * @param embedMessage The embedded message to be included in the response.
		 * @param actionRow The action row containing interactive components to be included.
		 * @return A CommandResponse configured for public interaction.
		 */
		@JvmStatic
		fun ofPublicInteractionMessage(embedMessage: MessageEmbed, actionRow: ActionRow) =
			CommandResponse(listOf(embedMessage), listOf(actionRow), false, null)

		/**
		 * Creates a [CommandResponse] for a public message.
		 *
		 * This method constructs a response containing only the provided embedded message, intended for a public
		 * Discord channel without any interactive components (action rows).
		 *
		 * @param embedMessage The embedded message to be included in the response.
		 * @return A CommandResponse configured for a public message without interaction components.
		 */
		@JvmStatic
		fun ofPublicMessage(embedMessage: MessageEmbed) = CommandResponse(listOf(embedMessage), emptyList(), false, null)

		/**
		 * Creates a [CommandResponse] for a private interaction message.
		 *
		 * This method constructs a response intended for a specific user, including the provided embed message and action
		 * row.
		 *
		 * @param embedMessage The embedded message to be included in the response.
		 * @param actionRow The action row containing interactive components to be included.
		 * @param userId The ID of the user to whom the private message should be sent.
		 * @return A CommandResponse configured for private interaction.
		 */
		@JvmStatic
		fun ofPrivateInteractionMessage(embedMessage: MessageEmbed, actionRow: ActionRow, userId: Long) =
			CommandResponse(listOf(embedMessage), listOf(actionRow), true, userId)

		/**
		 * Creates a [CommandResponse] for a private message.
		 *
		 * This method constructs a response that contains only an embedded message to be sent as a private message to the
		 * specified user.
		 *
		 * @param embedMessage The embedded message to be included in the private message.
		 * @param userId The ID of the user to whom the private message should be sent.
		 * @return A CommandResponse configured for a private message.
		 */
		@JvmStatic
		fun ofPrivateMessage(embedMessage: MessageEmbed, userId: Long) =
			CommandResponse(listOf(embedMessage), emptyList(), true, userId)
	}
}
