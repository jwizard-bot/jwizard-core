package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
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

@JdaCommand(Command.QUEUE_CLEAR)
class ClearQueueCmd(
	commandEnvironment: CommandEnvironmentBean
) : DjCommandBase(commandEnvironment) {

	companion object {
		private val log = logger<ClearQueueCmd>()
	}

	override val shouldOnSameChannelWithBot = true
	override val queueShouldNotBeEmpty = true

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val queueTrackScheduler = manager.state.queueTrackScheduler

		val queueSize = queueTrackScheduler.queue.clearAndGetSize()
		log.jdaInfo(context, "Queue was cleared. Removed: %d audio tracks from queue.", queueSize)

		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.CLEAR_QUEUE,
				args = mapOf("countOfTracks" to queueSize),
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
