/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction.component

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.command.interaction.ButtonInteractionHandler
import pl.jwizard.jwc.command.interaction.InteractionButton
import pl.jwizard.jwc.command.interaction.InteractionResponse
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean

/**
 * Paginator class for managing paginated content in a Discord bot.
 *
 * This class handles pagination by creating buttons to navigate through multiple pages of content, represented as
 * [MessageEmbed] objects.
 *
 * @property context The command context containing information about the command.
 * @property i18nBean The internationalization bean for translating button labels.
 * @property eventQueueBean The event queue manager for handling events.
 * @property jdaColorStoreBean The color store bean for managing colors in embeds.
 * @property pages The list of MessageEmbed objects representing the pages of content.
 * @author Miłosz Gilga
 */
class Paginator(
	private val context: CommandContext,
	private val i18nBean: I18nBean,
	private val eventQueueBean: EventQueueBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
	private val pages: List<MessageEmbed>,
) : ButtonInteractionHandler(i18nBean, eventQueueBean) {

	companion object {
		/**
		 * ID for the current page button.
		 */
		private const val CURRENT_BUTTON_ID = "current"

		/**
		 * Separator used in the current button label to indicate page numbers.
		 */
		private const val PAGE_SEPARATOR = "/"
	}

	/**
	 * Initializes the paginator by starting the event listener if there are multiple pages. Returns the first page of
	 * the paginator.
	 *
	 * @return The first MessageEmbed representing the initial page.
	 */
	fun initPaginator(): MessageEmbed {
		if (pages.size > 1) {
			initEvent()
		}
		return pages[0]
	}

	/**
	 * Creates an ActionRow containing pagination buttons if there is more than one page.
	 *
	 * @return An ActionRow of buttons, or null if only one page exists.
	 */
	fun createPaginatorButtonsRow() = if (pages.size > 1) {
		createPaginatorButtonsRow(1)
	} else {
		null
	}

	/**
	 * Creates an ActionRow of buttons for pagination based on the current page number.
	 *
	 * @param pageNumber The current page number to create buttons for.
	 * @return An ActionRow containing pagination buttons.
	 */
	private fun createPaginatorButtonsRow(pageNumber: Int): ActionRow {
		val disablePrev = pageNumber == 1
		val disableNext = pageNumber == pages.size

		val firstBtn = createButton(InteractionButton.FIRST, context.guildLanguage, disablePrev)
		val prevBtn = createButton(InteractionButton.PREV, context.guildLanguage, disablePrev)
		val currentBtn = createButton(CURRENT_BUTTON_ID, "$pageNumber$PAGE_SEPARATOR${pages.size}", true)
		val nextBtn = createButton(InteractionButton.NEXT, context.guildLanguage, disableNext)
		val lastBtn = createButton(InteractionButton.LAST, context.guildLanguage, disableNext)

		return ActionRow.of(firstBtn, prevBtn, currentBtn, nextBtn, lastBtn)
	}

	/**
	 * The buttons that this handler will respond to.
	 */
	override val runForButtons = arrayOf(
		InteractionButton.FIRST,
		InteractionButton.PREV,
		InteractionButton.NEXT,
		InteractionButton.LAST
	)

	/**
	 * Executes the button interaction event to navigate through pages. Determines the current page based on the pressed
	 * button and returns an updated InteractionResponse.
	 *
	 * @param event The button interaction event.
	 * @return An InteractionResponse containing the callback for editing the message and the refreshable status.
	 */
	override fun executeEvent(event: ButtonInteractionEvent): InteractionResponse {
		val currentBtn = event.message.buttons.find { getComponentId(it.id) == CURRENT_BUTTON_ID }
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
}
