package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.QUEUE_INFINITE)
internal class InfinitePlaylistCmd(
	commandEnvironment: CommandEnvironment,
) : DjCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<InfinitePlaylistCmd>()
	}

	override val shouldPlayingMode = true
	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val queueTrackScheduler = manager.state.queueTrackScheduler

		val isInLoop = queueTrackScheduler.audioRepeat.togglePlaylistLoop()
		log.jdaInfo(context, "Current playlist infinite playing state: %s.", isInLoop.toString())

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = if (isInLoop) {
					I18nResponseSource.ADD_PLAYLIST_TO_INFINITE_LOOP
				} else {
					I18nResponseSource.REMOVED_PLAYLIST_FROM_INFINITE_LOOP
				},
				args = mapOf("playlistLoopCmd" to Command.INFINITE.parseWithPrefix(context)),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
