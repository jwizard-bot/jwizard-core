package pl.jwizard.jwc.api.vote

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOTE_STOP)
class VoteStopAndClearQueueCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase(voterEnvironment, commandEnvironment) {
	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	override val initMessage = I18nResponseSource.VOTE_STOP_CLEAR_QUEUE
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_STOP_CLEAR_QUEUE

	override fun executeMusicVote(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	) = MusicVoterResponse(onSuccess = {
		val playingTrack = manager.cachedPlayer?.track
		val queueTrackScheduler = manager.state.queueTrackScheduler

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, it)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = queueTrackScheduler.stopAndDestroy(),
			onSuccess = {
				val queueSize = queueTrackScheduler.queue.size
				log.jdaInfo(
					context,
					"Stop current track: %s and clear queue via voting. Removed: %d results.",
					playingTrack?.qualifier,
					queueSize,
				)
				val (i18nSourceKey, args) = if (playingTrack == null) {
					Pair(I18nResponseSource.CLEAR_QUEUE, mapOf("countOfTracks" to queueSize))
				} else {
					Pair(
						I18nResponseSource.SKIPPED_CURRENT_TRACK_AND_CLEAR_QUEUE,
						mapOf("currentTrack" to playingTrack.mdTitleLink),
					)
				}
				createVoteSuccessMessage(context)
					.setDescription(i18nSourceKey, args)
					.apply { playingTrack?.let { setArtwork(it.thumbnailUrl) } }
					.setColor(JdaColor.PRIMARY)
					.build()
			},
		)
	})
}
