/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow

/**
 * Class representing a response to a command in a Discord bot. The response can include embedded messages, action
 * components (buttons, dropdowns), and other settings related to the command's response.
 *
 * @property embedMessages A list of embedded messages (MessageEmbed) sent in the response.
 * @property actionRows A list of ActionRows containing interaction components (ex. buttons, dropdown menus).
 * @property disposeComponents A flag indicating whether the interaction components should be disabled after execution.
 * @property afterSendAction A lambda function that will be executed after the message is sent. It receives the send
 *           Message as an argument.
 */
class CommandResponse private constructor(
	val embedMessages: List<MessageEmbed>,
	val actionRows: List<ActionRow>,
	val disposeComponents: Boolean,
	val afterSendAction: (Message) -> Unit,
) {

	/**
	 * Creates a copy of the current CommandResponse with the provided embedded messages and action rows.
	 *
	 * @param embedMessages The new list of embedded messages for the response.
	 * @param actionRows The new list of ActionRows (buttons, dropdowns, etc.) for the response.
	 * @return A new CommandResponse instance with the updated values.
	 */
	fun copy(embedMessages: List<MessageEmbed>, actionRows: List<ActionRow>) =
		CommandResponse(embedMessages, actionRows, disposeComponents, afterSendAction)

	/**
	 * Builder class for constructing a CommandResponse instance with various options.
	 */
	class Builder {

		/**
		 * A list of embedded messages (MessageEmbed) to be included in the response.
		 */
		private var embedMessages: List<MessageEmbed> = emptyList()

		/**
		 * A list of ActionRows (buttons, dropdowns, etc.) to be included in the response.
		 */
		private var actionRows: List<ActionRow> = emptyList()

		/**
		 * A flag indicating whether interaction components (buttons, etc.) should be disabled after execution.
		 */
		private var disposeComponents: Boolean = true

		/**
		 * A lambda function to be executed after the message is sent. Receives the send Message as an argument.
		 */
		private var onSendAction: (Message) -> Unit = {}

		/**
		 * Adds embedded messages (MessageEmbed) to the response.
		 *
		 * @param embedMessages The embedded messages to be included in the response.
		 * @return The Builder instance for chaining.
		 */
		fun addEmbedMessages(vararg embedMessages: MessageEmbed) = apply { this.embedMessages = embedMessages.toList() }

		/**
		 * Adds ActionRows (buttons, dropdowns, etc.) to the response.
		 *
		 * @param actionRows The action rows to be included in the response.
		 * @return The Builder instance for chaining.
		 */
		fun addActionRows(vararg actionRows: ActionRow) = apply { this.actionRows = actionRows.toList() }

		/**
		 * Sets whether interaction components (buttons, etc.) should be disabled after certain of time.
		 *
		 * @param disposeComponents Flag indicating whether components should be disabled.
		 * @return The Builder instance for chaining.
		 */
		fun disposeComponents(disposeComponents: Boolean) = apply { this.disposeComponents = disposeComponents }

		/**
		 * Adds an action to be executed after the message is sent.
		 *
		 * @param onSendAction A lambda function to execute after sending the message, receiving the send Message.
		 * @return The Builder instance for chaining.
		 */
		fun onSendAction(onSendAction: (Message) -> Unit) = apply { this.onSendAction = onSendAction }

		/**
		 * Builds and returns a CommandResponse instance with the configured options.
		 *
		 * @return A new CommandResponse instance.
		 */
		fun build() = CommandResponse(embedMessages, actionRows, disposeComponents, onSendAction)
	}
}
