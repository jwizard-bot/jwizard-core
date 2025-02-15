package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument

@JdaCommand(Command.PLAY)
internal class PlayCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {
	override val shouldOnSameChannelWithBot = true
	override val shouldEnabledOnFirstAction = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val track = context.getArg<String>(Argument.TRACK)
		manager.loadAndPlay(track, context)
	}
}
