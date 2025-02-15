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

@JdaCommand(Command.REPEAT_CLEAR)
class ClearRepeatTrackCmd(
	commandEnvironment: CommandEnvironmentBean
) : MusicCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<ClearRepeatTrackCmd>()
	}

	override val shouldOnSameChannelWithBot = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		manager.state.queueTrackScheduler.updateCountOfRepeats(0)

		val currentPlayingTrack = manager.cachedPlayer?.track
		log.jdaInfo(
			context,
			"Repeating of current playing track: %s was removed.",
			currentPlayingTrack?.qualifier
		)

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.REMOVE_MULTIPLE_REPEATING_TRACK,
				args = mapOf(
					"track" to currentPlayingTrack?.mdTitleLink,
					"repeatingCmd" to Command.REPEAT_SET.parseWithPrefix(context),
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
