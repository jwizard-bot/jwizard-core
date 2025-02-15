package pl.jwizard.jwc.vote.music

import pl.jwizard.jwc.core.jda.command.TFutureResponse

data class MusicVoterResponse(
	val onSuccess: (response: TFutureResponse) -> Unit,
	val args: Map<String, Any?> = emptyMap(),
)
