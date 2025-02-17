package pl.jwizard.jwc.api

import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.dj.UnauthorizedDjException
import pl.jwizard.jwc.exception.dj.UnauthorizedDjOrSenderException

internal abstract class DjCommandBase(
	commandEnvironment: CommandEnvironment,
) : MusicCommandBase(commandEnvironment) {
	final override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse
	) {
		val (isSender, isDj, isSuperUser) = checkPermissions(context, manager)
		val isNormalUser = !isSender && !isDj && !isSuperUser

		// some DJ commands can be used by normal users only if all tracks in queue is from member
		// who's invoked command
		if (shouldAllowAlsoForAllContentSender) {
			val audioScheduler = manager.state.queueTrackScheduler
			val allTracksFromSameMember = if (audioScheduler.queue.isEmpty()) {
				// for single track
				manager.cachedPlayer?.track?.audioSender?.authorId == context.author.idLong
			} else {
				// for all tracks in queue, only if queue is not empty
				audioScheduler.queue.iterable.all { it.audioSender.authorId == context.author.idLong }
			}
			// check all tracks only for normal users
			if (!allTracksFromSameMember && isNormalUser) {
				throw UnauthorizedDjOrSenderException(context, context.djRoleName)
			}
		} else if (isNormalUser) {
			throw UnauthorizedDjException(context, context.djRoleName)
		}
		executeDj(context, manager, response)
	}

	// available command also for normal members (content senders)
	protected open val shouldAllowAlsoForAllContentSender = true

	protected abstract fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse
	)
}
