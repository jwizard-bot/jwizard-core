/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.vote

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

/**
 * Command to clear the current music queue via voting.
 *
 * This command allows users to initiate a vote to clear all tracks from the music queue. If the vote passes, the queue
 * will be emptied, removing all audio tracks.
 *
 * @param voterEnvironment The environment related to voting, including merged beans in single data class.
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.VCLEAR)
class VoteClearQueueCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase<GuildMusicManager>(voterEnvironment, commandEnvironment) {

	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override val initMessage = I18nResponseSource.VOTE_CLEAR_QUEUE
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_CLEAR_QUEUE

	/**
	 * Executes the music voting process for clearing the current queue.
	 *
	 * This method prepares the voting response, including the number of tracks currently in the queue.
	 *
	 * @param context The command context containing information about the command execution.
	 * @param manager The guild music manager responsible for handling music-related operations.
	 * @return A response containing the result of the music voting process.
	 */
	override fun executeMusicVote(context: CommandContext, manager: GuildMusicManager) = MusicVoterResponse(
		payload = manager,
		args = mapOf("countOfTracks" to manager.state.queueTrackScheduler.queue.size),
	)

	/**
	 * Handles actions to be performed after a successful voting operation to clear the queue.
	 *
	 * This method clears the queue and logs the action. It also creates a success message to inform users about the
	 * outcome of the clear operation.
	 *
	 * @param context The command context for executing the action.
	 * @param response The future response object to complete.
	 * @param payload The guild music manager that contains the current state of the queue.
	 */
	override fun afterSuccess(context: CommandContext, response: TFutureResponse, payload: GuildMusicManager) {
		val queueTrackScheduler = payload.state.queueTrackScheduler

		val queueSize = queueTrackScheduler.queue.clearAndGetSize()
		log.jdaInfo(context, "Queue was cleared by voting. Removed: %d audio tracks from queue.", queueSize)

		val message = createVoteSuccessMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.CLEAR_QUEUE,
				args = mapOf("countOfTracks" to queueSize),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
