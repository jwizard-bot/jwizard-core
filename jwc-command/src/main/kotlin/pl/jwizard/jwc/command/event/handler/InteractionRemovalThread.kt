/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event.handler

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import pl.jwizard.jwc.command.event.handler.InteractionRemovalThread.Companion.COUNT_OF_THREADS
import pl.jwizard.jwc.core.jvm.thread.JvmFixedPayloadThreadExecutor

/**
 * A thread executor that handles the disabling of interaction components in a given message.
 *
 * This class extends [JvmFixedPayloadThreadExecutor] to provide functionality for processing messages and disabling
 * their buttons and other interactive components based on specified conditions.
 *
 * @property COUNT_OF_THREADS The number of threads to use for handling interaction removals.
 * @author Miłosz Gilga
 */
class InteractionRemovalThread : JvmFixedPayloadThreadExecutor<Message>(COUNT_OF_THREADS) {

	companion object {
		/**
		 * The number of threads used for handling message interaction removals.
		 */
		private const val COUNT_OF_THREADS = 5
	}

	/**
	 * Executes the logic to disable interactive components of the given message.
	 *
	 * This method processes the provided message by disabling all buttons that are not of type [ButtonStyle.LINK] and
	 * any other non-button action components.
	 *
	 * @param payload The message containing the action components to be modified.
	 */
	override fun executeJvmThreadWithPayload(payload: Message) {
		val disabledComponents = payload.actionRows.map {
			ActionRow.of(it.actionComponents
				.filter { component -> (component is Button && component.style != ButtonStyle.LINK) || component !is Button }
				.map(ActionComponent::asDisabled)
			)
		}
		payload.editMessageComponents(disabledComponents).queue()
	}
}
