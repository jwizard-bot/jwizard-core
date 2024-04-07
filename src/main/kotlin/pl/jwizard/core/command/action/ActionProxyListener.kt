/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.action

import pl.jwizard.core.log.AbstractLoggingBean
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent

@Component
class ActionProxyListener(
	private val actionProxyHandler: ActionProxyHandler
) : AbstractLoggingBean(ActionProxyListener::class) {

	fun onPressButton(buttonClickEvent: ButtonClickEvent) {
		val handler = ActionComponent.entries.find { it.name == buttonClickEvent.componentId } ?: return
		handler.actionCallback(actionProxyHandler, buttonClickEvent)
	}
}
