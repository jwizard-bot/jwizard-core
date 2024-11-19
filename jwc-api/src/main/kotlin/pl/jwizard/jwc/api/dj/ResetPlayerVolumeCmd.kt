/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.dj

import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.async.AsyncUpdatableHook
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command for resetting the audio player's volume to the default level. This command resets the current volume of the
 * Lavalink player to a predefined default value specified in the guild's properties.
 *
 * @param commandEnvironment The environment context for the command execution.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.VOLUMECLS)
class ResetPlayerVolumeCmd(
	commandEnvironment: CommandEnvironmentBean,
) : DjCommandBase(commandEnvironment), AsyncUpdatableHook<LavalinkPlayer, PlayerUpdateBuilder, Int> {

	companion object {
		private val log = logger<ResetPlayerVolumeCmd>()
	}

	/**
	 * Executes the command to reset the volume of the music player.
	 *
	 * Retrieves the default volume level from the guild's properties and resets the Lavalink player volume to this value
	 * asynchronously. Sends the result of the operation to the user through the response object.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeDj(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val defaultVolume = environmentBean.getGuildProperty<Int>(GuildProperty.PLAYER_VOLUME, context.guild.idLong)

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setVolume(defaultVolume),
			payload = defaultVolume,
		)
	}

	/**
	 * Called when the async volume reset operation is successful. Creates a response embed message to inform the user
	 * that the volume has been reset to the default level.
	 *
	 * @param context The context of the command, including user interaction details.
	 * @param result The result of the async operation, which is the updated [LavalinkPlayer].
	 * @param payload The default volume level that was applied.
	 * @return A MessageEmbed containing a confirmation of the volume reset and the new volume level.
	 */
	override fun onAsyncSuccess(context: CommandContext, result: LavalinkPlayer, payload: Int): MessageEmbed {
		log.jdaInfo(context, "Reset volume to default value: %d%%.", payload)
		return createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.RESET_AUDIO_PLAYER_VOLUME,
				args = mapOf("defVolume" to payload),
			)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
