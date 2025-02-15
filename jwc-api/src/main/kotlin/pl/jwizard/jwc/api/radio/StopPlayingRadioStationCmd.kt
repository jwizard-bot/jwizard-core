package pl.jwizard.jwc.api.radio

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.RadioCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command

@JdaCommand(Command.RADIO_STOP)
internal class StopPlayingRadioStationCmd(
	commandEnvironment: CommandEnvironmentBean,
) : RadioCommandBase(commandEnvironment) {
	override val shouldOnSameChannelWithBot = true
	override val shouldRadioPlaying = true

	override fun executeRadio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val radioStreamScheduler = manager.state.radioStreamScheduler
		radioStreamScheduler.stopAndDestroy().subscribe()
	}
}
