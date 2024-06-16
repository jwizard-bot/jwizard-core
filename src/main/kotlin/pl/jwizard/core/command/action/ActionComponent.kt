/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.action

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

enum class ActionComponent(
	val actionCallback: (handler: ActionProxyHandler, event: ButtonInteractionEvent) -> Unit
) {
	UPDATE_CURRENT_PLAYING_EMBED_MESSAGE({ handler, event -> handler.updateCurrentPlayingEmbedMessage(event) }),
	UPDATE_RADIO_PLAYBACK_EMBED_MESSAGE({ handler, event -> handler.updateRadioPlaybackEmbedMessage(event) }),
	;
}
