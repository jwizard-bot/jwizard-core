/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.dj.UnauthorizedDjException
import pl.jwizard.jwc.exception.dj.UnauthorizedDjOrSenderException

/**
 * Base class for DJ-related commands in the music command system.
 *
 * This class provides the necessary checks and functionality for commands that require DJ permissions, allowing for
 * specific command execution based on the user's role in the context of music playback.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
abstract class DjCommandBase(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	/**
	 * Executes the DJ command.
	 *
	 * This method performs permission checks to determine if the user invoking the command is a DJ or has sufficient
	 * permissions. It throws exceptions if the user is not authorized to execute the command.
	 *
	 * If the [shouldAllowAlsoForAllContentSender] flag is set to true, content senders may also execute the command if
	 * they are the sender of all tracks in the queue.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling audio playback.
	 * @param response The future response object used to send the result of the command execution.
	 * @throws UnauthorizedDjException If the user is not a DJ and does not meet the conditions.
	 * @throws UnauthorizedDjOrSenderException If the user is a normal user and all tracks in the queue are sent by them.
	 */
	final override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val (isSender, isDj, isSuperUser) = checkPermissions(context, manager)
		val isNormalUser = !isSender && !isDj && !isSuperUser
		val djRoleName = context.djRoleName

		if (shouldAllowAlsoForAllContentSender) {
			val allFromOneUser = checkIfAllTracksIsFromSelectedMember(manager, context)
			if (allFromOneUser && isNormalUser) {
				throw UnauthorizedDjOrSenderException(context, djRoleName)
			}
		} else if (isNormalUser) {
			throw UnauthorizedDjException(context, djRoleName)
		}
		executeDj(context, manager, response)
	}

	/**
	 * Checks whether all tracks in the queue were added by the same user who invoked the command.
	 *
	 * This method is used to determine if the user is allowed to execute certain commands based on their ownership of
	 * all tracks in the queue. If the queue is empty, the method checks the sender of the currently playing track.
	 *
	 * @param manager The music manager responsible for handling audio playback.
	 * @param context The context of the command, containing user interaction details.
	 * @return `true` if all tracks were added by the same user; `false` otherwise.
	 */
	private fun checkIfAllTracksIsFromSelectedMember(manager: MusicManager, context: CommandContext): Boolean {
		val audioScheduler = manager.state.queueTrackScheduler
		if (audioScheduler.queue.size == 0) {
			return manager.getAudioSenderId(manager.cachedPlayer?.track) == context.author.idLong
		}
		return audioScheduler.queue.iterable.all { manager.getAudioSenderId(it) == context.author.idLong }
	}

	/**
	 * Flag indicating whether all content senders are allowed to execute the command under specific conditions.
	 */
	protected open val shouldAllowAlsoForAllContentSender = true

	/**
	 * Executes the specific DJ command functionality.
	 *
	 * This method must be implemented by subclasses to define the specific behavior of the DJ command.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling audio playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	protected abstract fun executeDj(context: CommandContext, manager: MusicManager, response: TFutureResponse)
}
