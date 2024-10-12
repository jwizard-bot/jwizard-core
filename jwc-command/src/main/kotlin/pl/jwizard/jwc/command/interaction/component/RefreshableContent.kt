/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction.component

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

/**
 * An interface representing content that can be refreshed in response to button interactions.
 *
 * Implementing classes should define how to refresh their content based on user interactions and update the response
 * messages accordingly.
 *
 * @param T The type of the payload that will be used for refreshing the content.
 * @author Miłosz Gilga
 */
interface RefreshableContent<T> {

	/**
	 * Refreshes the content when the associated button is clicked.
	 *
	 * This method is called when a refresh button interaction occurs. Implementations should update the provided
	 * response list with new content.
	 *
	 * @param event The button interaction event containing details about the interaction.
	 * @param response A mutable list that will be populated with the new MessageEmbed objects to be sent as a response
	 *        to the interaction.
	 * @param payload The data payload that may be required for refreshing the content.
	 */
	fun onRefresh(event: ButtonInteractionEvent, response: MutableList<MessageEmbed>, payload: T)
}
