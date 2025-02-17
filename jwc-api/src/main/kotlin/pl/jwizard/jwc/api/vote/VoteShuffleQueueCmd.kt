package pl.jwizard.jwc.api.vote

import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.vote.VoterEnvironment
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOTE_QUEUE_SHUFFLE)
internal class VoteShuffleQueueCmd(
	voterEnvironment: VoterEnvironment,
	commandEnvironment: CommandEnvironment,
) : MusicVoteCommandBase(voterEnvironment, commandEnvironment) {
	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override val initMessage = I18nResponseSource.VOTE_SHUFFLE_QUEUE
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_SHUFFLE_QUEUE

	override fun executeMusicVote(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	) = MusicVoterResponse(
		onSuccess = {
			val queue = manager.state.queueTrackScheduler.queue
			queue.shuffle()
			log.jdaInfo(context, "Current queue of: %d tracks was shuffled via voting.", queue.size)

			val message = createVoteSuccessMessage(context)
				.setDescription(
					i18nLocaleSource = I18nResponseSource.QUEUE_WAS_SHUFFLED,
					args = mapOf("showQueueCmd" to Command.QUEUE_SHOW.parseWithPrefix(context))
				)
				.setColor(JdaColor.PRIMARY)
				.build()

			val commandResponse = CommandResponse.Builder()
				.addEmbedMessages(message)
				.build()

			it.complete(commandResponse)
		},
		args = mapOf("queueTracksCount" to manager.state.queueTrackScheduler.queue.size),
	)
}
