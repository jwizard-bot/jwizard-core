/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.radio

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.RadioCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.radio.RadioStationNotExistsOrTurnedOffException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.radio.RadioStation

/**
 * Command that handles the process of playing a radio station in a voice channel. This command automatically joins
 * the bot to a voice channel and streams the selected radio station.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.RADIO_PLAY)
class PlayRadioStationCmd(commandEnvironment: CommandEnvironmentBean) : RadioCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true
	override val shouldRadioIdle = true
	override val shouldEnabledOnFirstAction = true

	/**
	 * Executes the logic to play a radio station in the guild's voice channel. It fetches the radio station based on the
	 * provided slug, joins the voice channel and streams the radio station.
	 *
	 * @param context The command context containing information about the command's execution environment.
	 * @param manager The guild music manager responsible for handling audio streaming.
	 * @param response A future response handler to manage command feedback asynchronously.
	 * @throws RadioStationNotExistsOrTurnedOffException If the requested radio station does not exist or is inactive.
	 */
	override fun executeRadio(context: GuildCommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val radioStationSlug = context.getArg<String>(Argument.RADIO_STATION)

		val radioStation = RadioStation.entries.find { it.textKey == radioStationSlug }
			?: throw RadioStationNotExistsOrTurnedOffException(context, radioStationSlug)

		manager.loadAndStream(radioStation, context)
	}
}
