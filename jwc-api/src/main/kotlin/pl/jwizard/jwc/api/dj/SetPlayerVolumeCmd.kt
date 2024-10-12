/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.mono.AsyncUpdatableHook
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.command.refer.CommandArgument
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.logger
import pl.jwizard.jwc.exception.audio.VolumeUnitsOutOfBoundsException

/**
 * Command to set the volume of the music player.
 *
 * This command allows users to adjust the volume of the Lavalink player within a valid range. It checks if the volume
 * provided by the user is within the acceptable range and updates the player's volume if valid.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(id = Command.SETVOLUME)
class SetPlayerVolumeCmd(
	commandEnvironment: CommandEnvironmentBean
) : DjCommandBase(commandEnvironment),
	AsyncUpdatableHook<LavalinkPlayer, PlayerUpdateBuilder, Pair<Int, MusicManager>> {

	companion object {
		private val log = logger<SetPlayerVolumeCmd>()
	}

	/**
	 * Executes the command to set a new volume for the Lavalink player.
	 *
	 * Retrieves the volume level from the command arguments, validates it against the maximum allowable volume (defined
	 * in the bot properties), and sets the player's volume if the value is valid. If the provided volume is outside the
	 * valid range, it throws a [VolumeUnitsOutOfBoundsException].
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val newVolume = context.getArg<Int>(CommandArgument.VOLUME)
		val maxVolume = environmentBean.getProperty<Int>(BotProperty.PLAYER_MAX_VOLUME)

		val currentVolume = manager.cachedPlayer?.volume
		if (newVolume < 0 || newVolume > maxVolume) {
			throw VolumeUnitsOutOfBoundsException(context, newVolume, 0, maxVolume)
		}
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			monoAction = manager.createdOrUpdatedPlayer.setVolume(newVolume),
			payload = Pair(currentVolume ?: 0, manager),
		)
	}

	/**
	 * Called when the async volume update operation is successful.
	 *
	 * Creates a response embed message to inform the user about the volume change. The message includes the previous
	 * volume level and the newly set volume.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param result The result of the async operation, which is the updated [LavalinkPlayer].
	 * @param payload A pair containing the previous volume level and the music manager.
	 * @return A MessageEmbed containing a confirmation of the volume change and the new volume level.
	 */
	override fun onAsyncSuccess(
		context: CommandContext,
		result: LavalinkPlayer,
		payload: Pair<Int, MusicManager>,
	): MessageEmbed {
		val (previousVolume, manager) = payload
		val currentVolume = manager.cachedPlayer?.volume
		log.jdaInfo(context, "Changed volume from: %d%% to: %d%%.", previousVolume, currentVolume)
		return createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SET_CURRENT_AUDIO_PLAYER_VOLUME,
				args = mapOf(
					"previousVolume" to previousVolume,
					"setVolume" to manager.cachedPlayer?.volume,
				),
			)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
