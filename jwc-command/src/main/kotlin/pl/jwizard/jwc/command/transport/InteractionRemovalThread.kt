package pl.jwizard.jwc.command.transport

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.components.ActionComponent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import pl.jwizard.jwc.core.thread.JvmFixedPayloadThreadExecutor

internal class InteractionRemovalThread : JvmFixedPayloadThreadExecutor<Message>(
	countOfThreads = 1,
) {
	override fun executeJvmThreadWithPayload(payload: Message) {
		val disabledComponents = payload.actionRows.map {
			ActionRow.of(
				it.actionComponents
					.filter { component ->
						// disable only button whose not links and any other components
						(component is Button && component.style != ButtonStyle.LINK) || component !is Button
					}
					.map(ActionComponent::asDisabled)
			)
		}
		payload.editMessageComponents(disabledComponents).queue()
	}
}
