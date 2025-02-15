package pl.jwizard.jwc.command.interaction

import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.RestAction

data class InteractionResponse(
	val interactionCallback: (InteractionHook) -> RestAction<*>,
	val refreshableEvent: Boolean,
)
