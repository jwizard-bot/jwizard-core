package pl.jwizard.jwc.command

import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse

interface GuildCommandHandler {
	fun execute(context: GuildCommandContext, response: TFutureResponse)

	fun isPrivate(context: GuildCommandContext): Long? = null
}
