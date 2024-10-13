/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction.component

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.command.interaction.ButtonInteractionHandler
import pl.jwizard.jwc.command.interaction.InteractionButton
import pl.jwizard.jwc.command.interaction.InteractionResponse
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean

/**
 * A component that allows for refreshing content in response to button interactions.
 *
 * This class is designed to handle button interactions specifically for refreshing content that can change based on
 * some input or conditions.
 *
 * @param T The type of the payload associated with the refreshable content.
 * @property i18nBean The internationalization bean for translating button labels.
 * @property eventQueueBean The event queue manager for handling events.
 * @property refreshableContent The content that can be refreshed.
 * @property payload The data payload that will be used for refreshing the content.
 * @author Miłosz Gilga
 */
class RefreshableComponent<T>(
	private val i18nBean: I18nBean,
	private val eventQueueBean: EventQueueBean,
	private val refreshableContent: RefreshableContent<T>,
	private val payload: T,
) : ButtonInteractionHandler(i18nBean, eventQueueBean) {

	/**
	 * The buttons that this handler will respond to.
	 */
	override val runForButtons = arrayOf(InteractionButton.REFRESH)

	/**
	 * Executes the button interaction event to refresh the content.
	 *
	 * This method retrieves the current state of the content and updates the message with the new content if necessary.
	 * If no new content is generated, it simply retrieves the original message.
	 *
	 * @param event The button interaction event.
	 * @return An InteractionResponse containing the callback for editing the message and the refreshable status.
	 */
	override fun executeEvent(event: ButtonInteractionEvent): InteractionResponse {
		val responseMessages = mutableListOf<MessageEmbed>()
		refreshableContent.onRefresh(event, responseMessages, payload)
		return InteractionResponse(
			interactionCallback = if (responseMessages.isEmpty()) {
				{ it.retrieveOriginal() }
			} else {
				{ it.editOriginalEmbeds(*(responseMessages.toTypedArray())) }
			},
			refreshableEvent = true
		)
	}

	/**
	 * Creates an ActionRow containing the refresh button for the given content context.
	 *
	 * @param content The command base context to determine language for the button label.
	 * @return An ActionRow containing the refresh button.
	 */
	fun createRefreshButtonRow(content: CommandBaseContext) =
		ActionRow.of(createButton(InteractionButton.REFRESH, content.guildLanguage))
}