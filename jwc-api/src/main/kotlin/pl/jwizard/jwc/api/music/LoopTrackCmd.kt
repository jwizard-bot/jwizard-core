package pl.jwizard.jwc.api.music

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.ext.mdTitleLink
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.INFINITE)
class LoopTrackCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<LoopTrackCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val shouldBeContentSenderOrSuperuser = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val isInLoop = manager.state.queueTrackScheduler.audioRepeat.toggleTrackLoop()
		val currentPlayingTrack = manager.cachedPlayer?.track
		log.jdaInfo(
			context,
			"Current infinite playing state: %s for track: %s.",
			isInLoop.toString(),
			currentPlayingTrack?.qualifier
		)
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = if (isInLoop) {
					I18nResponseSource.ADD_TRACK_TO_INFINITE_LOOP
				} else {
					I18nResponseSource.REMOVED_TRACK_FROM_INFINITE_LOOP
				},
				args = mapOf(
					"track" to currentPlayingTrack?.mdTitleLink,
					"loopCmd" to Command.INFINITE.parseWithPrefix(context),
				),
			)
			.setArtwork(currentPlayingTrack?.thumbnailUrl)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
