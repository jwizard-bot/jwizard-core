/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.RestAction

/**
 * Represents a response to an interaction event. This data class encapsulates the callback for handling interaction
 * responses and indicates whether the event is refreshable.
 *
 * @property interactionCallback A function that takes an [InteractionHook] as a parameter and returns a RestAction
 *           representing the interaction response to be executed.
 * @property refreshableEvent A boolean flag indicating whether the event can be refreshed. If true, the response can
 *           be updated or modified based on subsequent actions.
 * @author Miłosz Gilga
 */
data class InteractionResponse(
	val interactionCallback: (InteractionHook) -> RestAction<*>,
	val refreshableEvent: Boolean,
)
