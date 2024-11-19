/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import dev.arbjerg.lavalink.client.player.Track
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.async.AsyncUpdatableHook
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.ext.thumbnailUrl
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to skip the currently playing track in the music player.
 *
 * This command allows the user to skip the track that is currently playing. Once the track is skipped, the next track
 * in the queue will begin playing.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.SKIP)
class SkipTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment), AsyncUpdatableHook<Track> {

	companion object {
		private val log = logger<SkipTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	/**
	 * Executes the skip track command for the music player.
	 *
	 * This method retrieves the currently playing track and stops it, allowing the next track in the queue to begin
	 * playback. If there is no track currently playing, an [UnexpectedException] is thrown.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The guild music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeMusic(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val skippingTrack = manager.cachedPlayer?.track ?: throw UnexpectedException(context, "Skipping track is NULL.")

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.stopTrack(),
			payload = skippingTrack,
		)
	}

	/**
	 * Called when the async track skip operation is successful.
	 *
	 * This method creates a response embed message informing the user that the current track was skipped and the next
	 * track will be played. It includes the skipped track's details such as its title and a link.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param payload The track that was skipped.
	 * @return A MessageEmbed containing information about the skipped track and a confirmation of the skip action.
	 */
	override fun onAsyncSuccess(context: CommandContext, payload: Track): MessageEmbed {
		log.jdaInfo(context, "Current playing track: %s was skipped.", payload.qualifier)
		return createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SKIP_TRACK_AND_PLAY_NEXT,
				args = mapOf("skippedTrack" to payload.mdTitleLink),
			)
			.setArtwork(payload.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
