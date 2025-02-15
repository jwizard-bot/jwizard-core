package pl.jwizard.jwc.api.playlist

import pl.jwizard.jwc.api.CommandBase
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command

@JdaCommand(Command.PLAYLIST_ADDTRACK)
internal class AddPlayingTrackToPlaylistCmd(
	commandEnvironment: CommandEnvironmentBean,
) : CommandBase(commandEnvironment) {
	override fun execute(context: GuildCommandContext, response: TFutureResponse) {
		// TODO
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
