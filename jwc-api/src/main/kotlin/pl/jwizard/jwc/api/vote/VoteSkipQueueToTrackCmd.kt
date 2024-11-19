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
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackOffsetOutOfBoundsException
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

/**
 * Command to skip to a specific track in the queue via voting.
 *
 * This command allows users to initiate a vote to skip to a specific track position in the current queue. It checks
 * whether the specified position is within bounds and executes the skip operation if the vote is successful.
 *
 * @param voterEnvironment The environment related to voting, including merged beans in single data class.
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.VSKIPTO)
class VoteSkipQueueToTrackCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase<Pair<GuildMusicManager, Int>>(voterEnvironment, commandEnvironment),
	AsyncUpdatableHook<LavalinkPlayer, PlayerUpdateBuilder, Pair<Track, Int>> {

	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override val initMessage = I18nResponseSource.VOTE_SKIP_TO_TRACK
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_SKIP_TO_TRACK

	/**
	 * Executes the music voting process for skipping to a specific track position in the queue.
	 *
	 * This method retrieves the specified position from the command arguments and checks if it is within the bounds of
	 * the current queue. If valid, it prepares the voting response.
	 *
	 * @param context The command context containing information about the command execution.
	 * @param manager The guild music manager responsible for handling music-related operations.
	 * @return A response containing the result of the music voting process.
	 * @throws TrackOffsetOutOfBoundsException If the specified position is out of bounds of the queue.
	 */
	override fun executeMusicVote(
		context: CommandContext,
		manager: GuildMusicManager,
	): MusicVoterResponse<Pair<GuildMusicManager, Int>> {
		val position = context.getArg<Int>(Argument.POS)

		val queue = manager.state.queueTrackScheduler.queue
		if (queue.positionIsOutOfBounds(position)) {
			throw TrackOffsetOutOfBoundsException(context, position, queue.size)
		}
		return MusicVoterResponse(
			payload = Pair(manager, position),
			args = mapOf(
				"audioTrack" to manager.state.queueTrackScheduler.queue.size,
				"countOfTracks" to manager.state.queueTrackScheduler.queue.size,
				"countOfTracks" to manager.state.queueTrackScheduler.queue.size,
			)
		)
	}

	/**
	 * Handles actions to be performed after a successful voting operation to skip to a specific track.
	 *
	 * This method updates the player to start playing the track at the specified position in the queue.
	 *
	 * @param context The command context for executing the action.
	 * @param response The future response object to complete.
	 * @param payload The pair containing the guild music manager and the track position to skip to.
	 */
	override fun afterSuccess(context: CommandContext, response: TFutureResponse, payload: Pair<GuildMusicManager, Int>) {
		val (manager, position) = payload
		val currentTrack = manager.state.queueTrackScheduler.queue.skipToPosition(position)!!

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setTrack(currentTrack),
			payload = Pair(currentTrack, position),
		)
	}

	/**
	 * Creates a message embed to be displayed after a successful async operation.
	 *
	 * This method generates an embed message that informs users about the result of the voting, including details about
	 * the track that is now playing.
	 *
	 * @param context The command context for creating the message.
	 * @param result The resulting Lavalink player after the async operation.
	 * @param payload The pair containing the track that was selected and its position.
	 * @return A message embed detailing the outcome of the skip operation.
	 */
	override fun onAsyncSuccess(
		context: CommandContext,
		result: LavalinkPlayer,
		payload: Pair<Track, Int>,
	): MessageEmbed {
		val (currentTrack, position) = payload
		log.jdaInfo(
			context,
			"Skipped: %d tracks in queue by voting and started playing: %s.",
			position,
			currentTrack.qualifier,
		)
		return createVoteSuccessMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.SKIP_TO_SELECT_TRACK_POSITION,
				args = mapOf(
					"countOfSkippedTracks" to position - 1,
					"currentTrack" to currentTrack.mdTitleLink,
				)
			)
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
