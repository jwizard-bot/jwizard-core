/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument

/**
 * Command to play a specified track in the music player.
 *
 * This command allows the user to play a track by providing its identifier. It ensures that the bot is in the same
 * voice channel as the user and automatically joins the channel if it isn't already connected.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.PLAY)
class PlayCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true
	override val shouldAutoJoinBotToChannel = true

	/**
	 * Executes the play command to load and play the specified track.
	 *
	 * This method retrieves the track identifier from the command context, ensures the bot joins the voice channel if
	 * necessary, and then loads and plays the specified track using the MusicManager.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val track = context.getArg<String>(Argument.TRACK)
		joinAndOpenAudioConnection(context)
		manager.loadAndPlay(track, context)
	}
}
