package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.context.GlobalCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse

interface GlobalCommandHandler {
	fun executeGlobal(context: GlobalCommandContext, response: TFutureResponse)
}
