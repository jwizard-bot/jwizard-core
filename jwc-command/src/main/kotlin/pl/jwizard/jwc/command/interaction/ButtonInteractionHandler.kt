package pl.jwizard.jwc.command.interaction

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.internal.interactions.component.ButtonImpl
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCache
import pl.jwizard.jwc.core.jda.event.queue.EventQueue
import pl.jwizard.jwc.core.jda.event.queue.EventQueueListener
import pl.jwizard.jwl.i18n.I18n
import java.util.concurrent.TimeUnit

abstract class ButtonInteractionHandler(
	private val i18n: I18n,
	private val eventQueue: EventQueue,
	private val botEmojisCache: BotEmojisCache,
) : EventQueueListener<ButtonInteractionEvent>, Component() {
	fun initEvent() {
		eventQueue.waitForEvent(ButtonInteractionEvent::class, this)
	}

	fun initTimeoutEvent(timeoutSec: Long) {
		eventQueue.waitForScheduledEvent(
			ButtonInteractionEvent::class,
			this,
			timeoutSec,
			TimeUnit.SECONDS
		)
	}

	protected fun createButton(
		interactionButton: InteractionButton,
		lang: String,
		args: Map<String, Any?> = emptyMap(),
		disabled: Boolean = false,
		style: ButtonStyle = ButtonStyle.SECONDARY,
	) = ButtonImpl(
		createComponentId(interactionButton.id),
		i18n.t(interactionButton.i18nSource, lang, args),
		style,
		disabled,
		interactionButton.emoji?.toEmoji(botEmojisCache)
	)

	protected fun createButton(
		interactionButton: InteractionButton,
		lang: String,
		disabled: Boolean = false,
		style: ButtonStyle = ButtonStyle.SECONDARY,
	) = createButton(interactionButton, lang, emptyMap(), disabled, style)

	protected fun createButton(
		id: String,
		label: String,
		disabled: Boolean = false,
		style: ButtonStyle = ButtonStyle.SECONDARY,
	) = ButtonImpl(createComponentId(id), label, style, disabled, null)

	// run event only for selected buttons in superclasses
	final override fun onPredicateExecuteEvent(
		event: ButtonInteractionEvent,
	) = runForButtons.any { it.id == getComponentId(event.componentId) }

	final override fun onEvent(event: ButtonInteractionEvent) {
		val (interactionCallback, refreshableEvent) = executeEvent(event)
		if (event.isAcknowledged) {
			// if event is already acknowledged, use existing event hook
			interactionCallback(event.hook).queue { refreshEvent(refreshableEvent) }
		} else {
			// otherwise create deferred event
			event.deferEdit().queue {
				interactionCallback(it).queue { refreshEvent(refreshableEvent) }
			}
		}
	}

	private fun refreshEvent(refreshable: Boolean) {
		if (refreshable) {
			initEvent()
		}
	}

	// Declared buttons which be accessed via this event
	protected abstract val runForButtons: Array<InteractionButton>

	// Executes after click button
	protected abstract fun executeEvent(event: ButtonInteractionEvent): InteractionResponse
}
