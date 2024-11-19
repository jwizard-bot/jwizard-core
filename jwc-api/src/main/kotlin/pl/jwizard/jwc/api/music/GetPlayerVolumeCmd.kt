/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwl.command.Command

/**
 * Command to retrieve the current volume of the audio player.
 *
 * This command checks the current volume (in percentage) setting of the bot's audio player and returns it as an
 * embed message.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.GETVOLUME)
class GetPlayerVolumeCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true

	/**
	 * Executes the command to fetch the current volume (percentage) of the player.
	 *
	 * This method retrieves the volume of the currently active audio player and sends an embed message displaying the
	 * volume percentage level.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.GET_CURRENT_AUDIO_PLAYER_VOLUME,
				args = mapOf("currentVolume" to manager.cachedPlayer?.volume)
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
