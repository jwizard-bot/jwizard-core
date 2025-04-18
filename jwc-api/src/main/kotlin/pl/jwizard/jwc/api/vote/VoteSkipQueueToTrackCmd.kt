package pl.jwizard.jwc.api.vote

import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.track.TrackOffsetOutOfBoundsException
import pl.jwizard.jwc.vote.VoterEnvironment
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOTE_SKIPTO)
internal class VoteSkipQueueToTrackCmd(
	voterEnvironment: VoterEnvironment,
	commandEnvironment: CommandEnvironment,
) : MusicVoteCommandBase(voterEnvironment, commandEnvironment) {
	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override val initMessage = I18nResponseSource.VOTE_SKIP_TO_TRACK
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_SKIP_TO_TRACK

	override fun executeMusicVote(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	): MusicVoterResponse {
		val position = context.getArg<Int>(Argument.POS)

		val queue = manager.state.queueTrackScheduler.queue
		if (queue.positionIsOutOfBounds(position)) {
			throw TrackOffsetOutOfBoundsException(context, position, queue.size)
		}
		return MusicVoterResponse(
			onSuccess = {
				val currentTrack = manager.state.queueTrackScheduler.queue.skipToPosition(position)!!
				val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, it)
				asyncUpdatableHandler.performAsyncUpdate(
					asyncAction = manager.createdOrUpdatedPlayer.setTrack(currentTrack),
					onSuccess = {
						log.jdaInfo(
							context,
							"Skipped: %d tracks in queue by voting and started playing: %s.",
							position,
							currentTrack.qualifier,
						)
						createVoteSuccessMessage(context)
							.setDescription(
								i18nLocaleSource = I18nResponseSource.SKIP_TO_SELECT_TRACK_POSITION,
								args = mapOf(
									"countOfSkippedTracks" to position - 1,
									"currentTrack" to currentTrack.mdTitleLink,
								)
							)
							.setArtwork(currentTrack.thumbnailUrl)
							.setColor(JdaColor.PRIMARY)
							.build()
					},
				)
			},
			args = mapOf(
				"audioTrack" to manager.cachedPlayer?.track?.mdTitleLink,
				"nextAudioTrack" to queue.getTrackByPosition(position).mdTitleLink,
				"countOfSkipped" to position - 1,
			)
		)
	}
}
