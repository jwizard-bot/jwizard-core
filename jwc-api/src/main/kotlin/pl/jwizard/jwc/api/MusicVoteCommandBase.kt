/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.i18n.source.I18nVotingSource
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.vote.I18nVoterResponse
import pl.jwizard.jwc.vote.VoterContent
import pl.jwizard.jwc.vote.music.MusicVoterComponent
import pl.jwizard.jwc.vote.music.MusicVoterResponse

/**
 * Abstract base class for music voting commands.
 *
 * This class provides a template for implementing music voting functionality in Discord commands, handling the
 * initialization of voting messages and responses. It extends [MusicCommandBase] and implements [VoterContent] to
 * facilitate interaction with the voting process.
 *
 * @param T The type of the payload associated with the music voting process.
 * @property commandEnvironment The environment in which the command is executed, providing access to various services
 *           and functionalities.
 * @author Miłosz Gilga
 */
abstract class MusicVoteCommandBase<T : Any>(
	commandEnvironment: CommandEnvironmentBean
) : MusicCommandBase(commandEnvironment), VoterContent<T> {

	/**
	 * Executes the music command and initializes the voting process.
	 *
	 * This method is called to handle the execution of the music command and sets up the voting mechanism. It creates an
	 * instance of [MusicVoterComponent] with the specified context and prepares the initial voting message and action
	 * row.
	 *
	 * @param context The command context containing information about the command execution.
	 * @param manager The music manager responsible for handling music-related operations.
	 * @param response A future response object to be completed with the command response.
	 */
	final override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val voterResponse = executeMusicVote(context, manager)
		val i18nResponse = I18nVoterResponse.Builder<T>()
			.setInitMessage(initMessage, voterResponse.args)
			.setFailedMessage(failedMessage, voterResponse.args)
			.setPayload(voterResponse.payload)
			.build()
		val musicVoter = MusicVoterComponent(context, i18nResponse, this, commandEnvironment)
		val (message, actionRow) = musicVoter.createInitVoterMessage()
		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(actionRow)
			.onSendAction { musicVoter.initVoter(it) }
			.build()
		response.complete(commandResponse)
	}

	/**
	 * Creates a success message for when voting succeeds. This method constructs an embed message indicating that the
	 * voting process was successful, using the specified command context.
	 *
	 * @param context The command context for creating the message.
	 * @return An embed message indicating successful voting.
	 */
	protected fun createVoteSuccessMessage(context: CommandContext) = createEmbedMessage(context)
		.setTitle(I18nVotingSource.ON_SUCCESS_VOTING)

	/**
	 * An abstract property representing the initial message to be displayed for voting.
	 *
	 * This should return an instance of [I18nResponseSource] that contains the localization key for the initial voting
	 * message.
	 */
	protected abstract val initMessage: I18nResponseSource

	/**
	 * An abstract property representing the message to be displayed on voting failure.
	 *
	 * This should return an instance of [I18nResponseSource] that contains the localization key for the failure message.
	 */
	protected abstract val failedMessage: I18nResponseSource

	/**
	 * Executes the music voting process.
	 *
	 * This method is to be implemented by subclasses to handle the actual music voting logic and return a
	 * [MusicVoterResponse] containing the result of the vote.
	 *
	 * @param context The command context for executing the vote.
	 * @param manager The music manager responsible for managing music-related operations.
	 * @return A response containing the result of the music voting process.
	 */
	protected abstract fun executeMusicVote(context: CommandContext, manager: MusicManager): MusicVoterResponse<T>
}
