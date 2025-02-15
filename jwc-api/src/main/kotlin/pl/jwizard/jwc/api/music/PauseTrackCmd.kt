package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.jda.embed.PercentageIndicatorBar
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.millisToDTF
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.PAUSE)
internal class PauseTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<PauseTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setPaused(true),
			onSuccess = {
				// should never have thrown this exception, but who knows
				val pausedTrack = manager.cachedPlayer?.track
					?: throw UnexpectedException(context, "Paused track is NULL.")

				val elapsedTime = manager.cachedPlayer?.position ?: 0
				log.jdaInfo(context, "Current playing track: %s was paused.", pausedTrack.qualifier)

				val percentageIndicatorBar = PercentageIndicatorBar(
					start = elapsedTime,
					total = pausedTrack.duration,
				)
				val messageBuilder = createEmbedMessage(context)
					.setDescription(
						i18nLocaleSource = I18nResponseSource.PAUSED_TRACK,
						args = mapOf(
							"track" to pausedTrack.mdTitleLink,
							"resumeCmd" to Command.RESUME.parseWithPrefix(context),
						),
					)
					.setValueField(percentageIndicatorBar.generateBar(), inline = false)
					.setKeyValueField(I18nAudioSource.PAUSED_TRACK_TIME, millisToDTF(elapsedTime))

				pausedTrack.let {
					messageBuilder.setKeyValueField(
						I18nAudioSource.PAUSED_TRACK_ESTIMATE_TIME,
						millisToDTF(it.duration - elapsedTime),
					)
					messageBuilder.setKeyValueField(
						I18nAudioSource.PAUSED_TRACK_TOTAL_DURATION,
						millisToDTF(it.duration)
					)
				}
				messageBuilder
					.setColor(JdaColor.PRIMARY)
					.setArtwork(pausedTrack.thumbnailUrl)

				messageBuilder.build()
			},
		)
	}
}
