package pl.jwizard.jwc.api.radio

import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.api.RadioCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.radio.RadioStationNotExistsOrTurnedOffException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.radio.RadioStation

@JdaCommand(Command.RADIO_PLAY)
internal class PlayRadioStationCmd(
	commandEnvironment: CommandEnvironment,
) : RadioCommandBase(commandEnvironment) {
	override val shouldOnSameChannelWithBot = true
	override val shouldRadioIdle = true
	override val shouldEnabledOnFirstAction = true

	override fun executeRadio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val radioStationSlug = context.getArg<String>(Argument.RADIO_STATION)

		// check, if passed radio station id exists in declared radio stations
		val radioStation = RadioStation.entries.find { it.textKey == radioStationSlug }
			?: throw RadioStationNotExistsOrTurnedOffException(context, radioStationSlug)

		manager.loadAndStream(radioStation, context)
	}
}
