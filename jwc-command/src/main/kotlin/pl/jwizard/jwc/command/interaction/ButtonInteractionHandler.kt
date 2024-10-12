/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueListener

/**
 * Abstract class for handling button interactions in a Discord bot.
 *
 * This class serves as a base for creating specific button interaction handlers, providing functionality for creating
 * buttons and managing interaction events.
 *
 * @property i18nButton The internationalization bean used for translating button labels.
 * @property eventQueueBean The event queue manager used for handling events.
 * @author Miłosz Gilga
 */
abstract class ButtonInteractionHandler(
	private val i18nButton: I18nBean,
	private val eventQueueBean: EventQueueBean,
) : EventQueueListener<ButtonInteractionEvent>, Component() {

	/**
	 * Initializes the event listener for button interactions. The handler will wait for button interaction events and
	 * process them accordingly.
	 */
	fun initEvent() {
		eventQueueBean.waitForEvent(ButtonInteractionEvent::class, this)
	}

	/**
	 * Creates a button with a label based on the provided interaction button enum.
	 *
	 * @param interactionButton The button enum representing the interaction type.
	 * @param lang The language to be used for the button label.
	 * @param disabled Whether the button should be disabled.
	 * @param style The style of the button.
	 * @return A Button instance with the specified properties.
	 */
	protected fun createButton(
		interactionButton: InteractionButton,
		lang: String,
		disabled: Boolean = false,
		style: ButtonStyle = ButtonStyle.SECONDARY,
	): Button {
		val label = i18nButton.t(interactionButton.i18nSource, lang)
		return ButtonImpl(createComponentId(interactionButton.id), label, style, disabled, null)
	}

	/**
	 * Creates a button with a custom label and ID.
	 *
	 * @param id The unique identifier for the button.
	 * @param label The label displayed on the button.
	 * @param disabled Whether the button should be disabled.
	 * @param style The style of the button.
	 * @return A Button instance with the specified properties.
	 */
	protected fun createButton(
		id: String,
		label: String,
		disabled: Boolean = false,
		style: ButtonStyle = ButtonStyle.SECONDARY,
	) = ButtonImpl(createComponentId(id), label, style, disabled, null)

	/**
	 * Checks if the event's component ID matches any of the buttons this handler is responsible for.
	 *
	 * @param event The button interaction event.
	 * @return True if the event's component ID matches, false otherwise.
	 */
	final override fun onPredicateExecuteEvent(event: ButtonInteractionEvent): Boolean {
		val componentId = getComponentId(event.componentId)
		return runForButtons.any { it.id == componentId }
	}

	/**
	 * Handles the button interaction event.
	 *
	 * @param event The button interaction event.
	 */
	final override fun onEvent(event: ButtonInteractionEvent) {
		val (interactionCallback, refreshableEvent) = executeEvent(event)
		if (event.isAcknowledged) {
			interactionCallback(event.hook).queue { refreshEvent(refreshableEvent) }
			return
		}
		event.deferEdit().queue {
			interactionCallback(it).queue { refreshEvent(refreshableEvent) }
		}
	}

	/**
	 * Refreshes the event listener if the refreshable flag is set.
	 *
	 * @param refreshable Indicates whether to re-initialize the event listener.
	 */
	private fun refreshEvent(refreshable: Boolean) {
		if (refreshable) {
			initEvent()
		}
	}

	/**
	 * An array of InteractionButton enums that this handler will respond to.
	 */
	protected abstract val runForButtons: Array<InteractionButton>

	/**
	 * Executes the event handling logic for a button interaction.
	 *
	 * @param event The button interaction event.
	 * @return An InteractionResponse containing the callback and refreshable status.
	 */
	protected abstract fun executeEvent(event: ButtonInteractionEvent): InteractionResponse
}
