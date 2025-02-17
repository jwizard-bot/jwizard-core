package pl.jwizard.jwc.command.interaction.component

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.command.interaction.ButtonInteractionHandler
import pl.jwizard.jwc.command.interaction.InteractionButton
import pl.jwizard.jwc.command.interaction.InteractionResponse
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.emoji.BotEmojisCache
import pl.jwizard.jwc.core.jda.event.queue.EventQueue
import pl.jwizard.jwl.i18n.I18n

class Paginator(
	private val context: CommandBaseContext,
	private val pages: List<MessageEmbed>,
	i18n: I18n,
	eventQueue: EventQueue,
	botEmojisCache: BotEmojisCache,
) : ButtonInteractionHandler(i18n, eventQueue, botEmojisCache) {
	companion object {
		private const val CURRENT_BUTTON_ID = "current"
		private const val PAGE_SEPARATOR = "/"
	}

	override val runForButtons = arrayOf(
		InteractionButton.FIRST,
		InteractionButton.PREV,
		InteractionButton.NEXT,
		InteractionButton.LAST
	)

	override fun executeEvent(event: ButtonInteractionEvent): InteractionResponse {
		val currentBtn = event.message.buttons.find { getComponentId(it.id) == CURRENT_BUTTON_ID }
		// extract current page from current button content
		val currentPage = if (currentBtn != null) {
			currentBtn.label.split(PAGE_SEPARATOR)[0].toInt()
		} else {
			1
		}
		val updatedPage = when (getComponentId(event.componentId)) {
			InteractionButton.FIRST.id -> 1
			InteractionButton.PREV.id -> currentPage - 1
			InteractionButton.NEXT.id -> currentPage + 1
			InteractionButton.LAST.id -> pages.size
			else -> currentPage
		}
		val buttons = createPaginatorButtonsRow(updatedPage)
		val content = pages[updatedPage - 1]

		return InteractionResponse(
			interactionCallback = { it.editOriginalEmbeds(content).setComponents(buttons) },
			refreshableEvent = true,
		)
	}

	val paginatorButtonsRow
		get() = if (pages.size > 1) {
			createPaginatorButtonsRow(1)
		} else {
			null
		}

	fun initPaginator(): MessageEmbed {
		if (pages.size > 1) {
			initEvent()
		}
		return pages[0] // return first page
	}

	private fun createPaginatorButtonsRow(pageNumber: Int): ActionRow {
		val disablePrev = pageNumber == 1
		val disableNext = pageNumber == pages.size

		val firstBtn = createButton(InteractionButton.FIRST, context.language, disablePrev)
		val prevBtn = createButton(InteractionButton.PREV, context.language, disablePrev)
		val currBtn = createButton(CURRENT_BUTTON_ID, "$pageNumber$PAGE_SEPARATOR${pages.size}", true)
		val nextBtn = createButton(InteractionButton.NEXT, context.language, disableNext)
		val lastBtn = createButton(InteractionButton.LAST, context.language, disableNext)

		return ActionRow.of(firstBtn, prevBtn, currBtn, nextBtn, lastBtn)
	}
}
