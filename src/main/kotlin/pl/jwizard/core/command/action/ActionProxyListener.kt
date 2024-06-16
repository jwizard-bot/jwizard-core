/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.action

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import org.springframework.stereotype.Component
import pl.jwizard.core.log.AbstractLoggingBean

@Component
class ActionProxyListener(
	private val actionProxyHandler: ActionProxyHandler
) : AbstractLoggingBean(ActionProxyListener::class) {

	fun onPressButton(event: ButtonInteractionEvent) {
		if (event.isFromGuild) {
			val handler = ActionComponent.entries.find { it.name == event.componentId } ?: return
			handler.actionCallback(actionProxyHandler, event)
		}
	}
}
