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

class RefreshableComponent(
	i18n: I18n,
	eventQueue: EventQueue,
	botEmojisCache: BotEmojisCache,
	private val onRefresh: (response: MutableList<MessageEmbed>) -> Unit,
) : ButtonInteractionHandler(i18n, eventQueue, botEmojisCache) {
	override val runForButtons = arrayOf(InteractionButton.REFRESH)

	override fun executeEvent(event: ButtonInteractionEvent): InteractionResponse {
		val responseMessages = mutableListOf<MessageEmbed>()
		onRefresh(responseMessages)
		return InteractionResponse(
			// if user not pass any response messages, get original (keep without editing)
			interactionCallback = if (responseMessages.isEmpty()) {
				{ it.retrieveOriginal() }
			} else {
				{ it.editOriginalEmbeds(*(responseMessages.toTypedArray())) }
			},
			refreshableEvent = true
		)
	}

	fun createRefreshButtonRow(
		content: CommandBaseContext,
	) = ActionRow.of(createRefreshButton(content))

	fun createRefreshButton(
		content: CommandBaseContext,
	) = createButton(InteractionButton.REFRESH, content.language)
}
