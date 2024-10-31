/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.playlist

import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command

/**
 * TODO
 *
 * @param commandEnvironment
 * @author Miłosz Gilga
 */
@JdaCommand(Command.SHOWPLTRACKS)
class ShowPlaylistTracksCmd(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * TODO
	 *
	 * @param context
	 * @param response
	 * @return
	 */
	override fun execute(context: CommandContext, response: TFutureResponse) {

		val message = createEmbedMessage(context)
			.setDescription("Not implemented yet")
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
