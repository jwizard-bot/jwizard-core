/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.vote

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.async.TUpdatableCommandHook
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to stop the currently playing track and clear the queue via voting.
 *
 * This command allows users to initiate a vote to stop the currently playing track and clear the queue in a music
 * player. The command ensures that the bot is in the same voice channel as the user and that voting messages are
 * properly localized.
 *
 * @param voterEnvironment The environment related to voting, including merged beans in single data class.
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.VOTE_STOP)
class VoteStopAndClearQueueCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase<GuildMusicManager>(voterEnvironment, commandEnvironment),
	TUpdatableCommandHook<Pair<Track?, Int>> {

	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	override val initMessage = I18nResponseSource.VOTE_STOP_CLEAR_QUEUE
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_STOP_CLEAR_QUEUE

	/**
	 * Executes the music voting process for stopping and clearing the queue.
	 *
	 * This method handles the execution of the voting mechanism and returns a [MusicVoterResponse] containing the
	 * current music manager as payload.
	 *
	 * @param context The command context containing information about the command execution.
	 * @param manager The guild music manager responsible for handling music-related operations.
	 * @return A response containing the result of the music voting process.
	 */
	override fun executeMusicVote(context: GuildCommandContext, manager: GuildMusicManager) =
		MusicVoterResponse(payload = manager)

	/**
	 * Handles actions to be performed after a successful voting operation.
	 *
	 * This method will stop the currently playing track and clear the queue based on the result of the voting process.
	 *
	 * @param context The command context for executing the action.
	 * @param response The future response object to complete.
	 * @param payload The guild music manager containing the current player state.
	 */
	override fun afterSuccess(context: GuildCommandContext, response: TFutureResponse, payload: GuildMusicManager) {
		val playingTrack = payload.cachedPlayer?.track
		val queueTrackScheduler = payload.state.queueTrackScheduler
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response, this)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = queueTrackScheduler.stopAndDestroy(),
			payload = Pair(playingTrack, queueTrackScheduler.queue.size),
		)
	}

	/**
	 * Creates a message embed to be displayed after a successful async operation.
	 *
	 * This method generates an embed message that informs users about the result of the voting, including details about
	 * the track that was playing and the number of tracks removed from the queue.
	 *
	 * @param context The command context for creating the message.
	 * @param payload A pair containing the currently playing track and the size of the queue.
	 * @return A message embed detailing the outcome of the stop and clear operation.
	 */
	override fun onAsyncSuccess(context: GuildCommandContext, payload: Pair<Track?, Int>): MessageEmbed {
		val (playingTrack, queueSize) = payload
		log.jdaInfo(
			context,
			"Stop current track: %s and clear queue via voting. Removed: %d results.",
			playingTrack?.qualifier,
			queueSize,
		)
		val (i18nSourceKey, args) = if (playingTrack == null) {
			Pair(I18nResponseSource.CLEAR_QUEUE, mapOf("countOfTracks" to queueSize))
		} else {
			Pair(I18nResponseSource.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE, mapOf("currentTrack" to playingTrack.mdTitleLink))
		}
		return createVoteSuccessMessage(context)
			.setDescription(i18nSourceKey, args)
			.apply { playingTrack?.let { setArtwork(it.thumbnailUrl) } }
			.setColor(JdaColor.PRIMARY)
			.build()
	}
}
