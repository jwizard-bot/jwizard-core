/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.action

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

interface ActionProxyHandler {
	fun updateCurrentPlayingEmbedMessage(buttonClickEvent: ButtonInteractionEvent)
	fun updateRadioPlaybackEmbedMessage(buttonClickEvent: ButtonInteractionEvent)
}
