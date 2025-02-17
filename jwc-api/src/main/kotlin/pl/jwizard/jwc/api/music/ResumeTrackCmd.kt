package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.name
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.RESUME)
internal class ResumeTrackCmd(
	commandEnvironment: CommandEnvironment,
) : MusicCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<ResumeTrackCmd>()
	}

	override val shouldPaused = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setPaused(false),
			onSuccess = {
				// should never have thrown this exception, but who knows
				val resumedTrack = manager.cachedPlayer?.track
					?: throw UnexpectedException(context, "Resumed track is NULL.")

				log.jdaInfo(context, "Current paused track: %s was resumed.", resumedTrack.qualifier)

				createEmbedMessage(context)
					.setDescription(
						i18nLocaleSource = I18nResponseSource.RESUME_TRACK,
						args = mapOf(
							"track" to resumedTrack.mdTitleLink,
							"invoker" to context.author.name,
							"pauseCmd" to Command.PAUSE.parseWithPrefix(context),
						),
					)
					.setArtwork(resumedTrack.thumbnailUrl)
					.setColor(JdaColor.PRIMARY)
					.build()
			},
		)
	}
}
