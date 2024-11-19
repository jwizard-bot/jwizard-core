/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.vote

import dev.arbjerg.lavalink.client.player.LavalinkPlayer
import dev.arbjerg.lavalink.client.player.PlayerUpdateBuilder
import dev.arbjerg.lavalink.client.player.Track
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicVoteCommandBase
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
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to skip the currently playing track via voting.
 *
 * This command allows users to initiate a vote to skip the currently playing track. It ensures that the bot is in the
 * same voice channel and that the current track is available to skip. After the voting process, if successful, the
 * track is skipped and the next track in the queue is played.
 *
 * @param voterEnvironment The environment related to voting, including merged beans in single data class.
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.VSKIP)
class VoteSkipTrackCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase<GuildMusicManager>(voterEnvironment, commandEnvironment),
	AsyncUpdatableHook<LavalinkPlayer, PlayerUpdateBuilder, Track> {

	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true

	override val initMessage = I18nResponseSource.VOTE_SKIP_TRACK
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_SKIP_TRACK

	/**
	 * Executes the music voting process for skipping the current track.
	 *
	 * This method retrieves the currently playing track from the music manager and prepares the voting response,
	 * including track details for localization.
	 *
	 * @param context The command context containing information about the command execution.
	 * @param manager The guild music manager responsible for handling music-related operations.
	 * @return A response containing the result of the music voting process.
	 * @throws UnexpectedException if the currently playing track is null.
	 */
	override fun executeMusicVote(
		context: CommandContext,
		manager: GuildMusicManager,
	): MusicVoterResponse<GuildMusicManager> {
		val track = manager.cachedPlayer?.track ?: throw UnexpectedException(context, "Playing track is NULL.")
		return MusicVoterResponse(
			payload = manager,
			args = mapOf("audioTrack" to track.mdTitleLink)
		)
	}

	/**
	 * Handles actions to be performed after a successful voting operation.
	 *
	 * This method will stop the currently playing track based on the result of the voting process.
	 *
	 * @param context The command context for executing the action.
	 * @param response The future response object to complete.
	 * @param payload The guild music manager containing the current player state.
	 */
	override fun afterSuccess(context: CommandContext, response: TFutureResponse, payload: GuildMusicManager) {
		val track = payload.cachedPlayer?.track ?: return
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = payload.createdOrUpdatedPlayer.stopTrack(),
			payload = track,
		)
	}

	/**
	 * Creates a message embed to be displayed after a successful async operation.
	 *
	 * This method generates an embed message that informs users about the result of the voting, including details about
	 * the track that was skipped.
	 *
	 * @param context The command context for creating the message.
	 * @param result The resulting Lavalink player after the async operation.
	 * @param payload The track that was skipped.
	 * @return A message embed detailing the outcome of the skip operation.
	 */
	override fun onAsyncSuccess(context: CommandContext, result: LavalinkPlayer, payload: Track): MessageEmbed {
		log.jdaInfo(context, "Current playing track: %s was skipped by voting.", payload.qualifier)
		return createVoteSuccessMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SKIP_TRACK_AND_PLAY_NEXT,
				args = mapOf("skippedTrack" to payload.mdTitleLink),
			)
			.setArtwork(payload.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
