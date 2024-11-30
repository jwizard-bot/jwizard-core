/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.playlist

import pl.jwizard.jwc.api.CommandBase
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.command.GuildCommandHandler
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command

/**
 * TODO
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.PLAYLIST_MEMBER)
class ShowMemberPlaylistsCmd(
	commandEnvironment: CommandEnvironmentBean,
) : CommandBase(commandEnvironment), GuildCommandHandler {

	/**
	 * TODO
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun execute(context: GuildCommandContext, response: TFutureResponse) {

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
