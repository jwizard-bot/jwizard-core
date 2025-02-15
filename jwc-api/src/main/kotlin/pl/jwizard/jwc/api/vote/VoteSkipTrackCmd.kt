package pl.jwizard.jwc.api.vote

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicVoteCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwc.vote.VoterEnvironmentBean
import pl.jwizard.jwc.vote.music.MusicVoterResponse
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOTE_SKIP)
internal class VoteSkipTrackCmd(
	voterEnvironment: VoterEnvironmentBean,
	commandEnvironment: CommandEnvironmentBean,
) : MusicVoteCommandBase(voterEnvironment, commandEnvironment) {
	companion object {
		private val log = logger<VoteSkipTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true

	override val initMessage = I18nResponseSource.VOTE_SKIP_TRACK
	override val failedMessage = I18nResponseSource.FAILURE_VOTE_SKIP_TRACK

	override fun executeMusicVote(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	): MusicVoterResponse {
		// should be never throw, but who knows
		val track = manager.cachedPlayer?.track
			?: throw UnexpectedException(context, "Playing track is NULL.")
		return MusicVoterResponse(
			onSuccess = {
				val afterVoteTrack = manager.cachedPlayer?.track ?: return@MusicVoterResponse
				val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, it)
				asyncUpdatableHandler.performAsyncUpdate(
					asyncAction = manager.createdOrUpdatedPlayer.stopTrack(),
					onSuccess = {
						log.jdaInfo(
							context,
							"Current playing track: %s was skipped by voting.",
							afterVoteTrack.qualifier
						)
						createVoteSuccessMessage(context)
							.setDescription(
								i18nLocaleSource = I18nResponseSource.SKIP_TRACK_AND_PLAY_NEXT,
								args = mapOf("skippedTrack" to afterVoteTrack.mdTitleLink),
							)
							.setArtwork(track.thumbnailUrl)
							.setColor(JdaColor.PRIMARY)
							.build()
					},
				)
			},
			args = mapOf("audioTrack" to track.mdTitleLink)
		)
	}
}
