package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.SKIP)
class SkipTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<SkipTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		// should never have thrown this exception, but who knows
		val skippingTrack = manager.cachedPlayer?.track
			?: throw UnexpectedException(context, "Skipping track is NULL.")

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.stopTrack(),
			onSuccess = {
				log.jdaInfo(context, "Current playing track: %s was skipped.", skippingTrack.qualifier)
				createEmbedMessage(context)
					.setDescription(
						i18nLocaleSource = I18nResponseSource.SKIP_TRACK_AND_PLAY_NEXT,
						args = mapOf("skippedTrack" to skippingTrack.mdTitleLink),
					)
					.setArtwork(skippingTrack.thumbnailUrl)
					.setColor(JdaColor.PRIMARY)
					.build()
			},
		)
	}
}
