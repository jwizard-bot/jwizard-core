package pl.jwizard.jwc.api.vote

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOTE_QUEUE_CLEAR)
class VoteClearQueueCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase(voterEnvironment, commandEnvironment) {

	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override val initMessage = I18nResponseSource.VOTE_CLEAR_QUEUE
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_CLEAR_QUEUE

	override fun executeMusicVote(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	) = MusicVoterResponse(
		onSuccess = {
			val queueTrackScheduler = manager.state.queueTrackScheduler
			val queueSize = queueTrackScheduler.queue.clearAndGetSize()

			log.jdaInfo(
				context,
				"Queue was cleared by voting. Removed: %d audio tracks from queue.",
				queueSize,
			)

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

			it.complete(commandResponse)
		},
		args = mapOf("countOfTracks" to manager.state.queueTrackScheduler.queue.size),
	)
}
