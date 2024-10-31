/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.playlist

import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
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
@JdaCommand(Command.PLAYPL)
class LoadAndPlayPlaylistCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true
	override val shouldAutoJoinBotToChannel = true

	/**
	 * TODO
	 *
	 * @param context
	 * @param response
	 * @return
	 */
	override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
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
