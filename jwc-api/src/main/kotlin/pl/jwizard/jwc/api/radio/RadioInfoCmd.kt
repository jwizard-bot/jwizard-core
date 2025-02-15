package pl.jwizard.jwc.api.radio

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.RadioCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwc.exception.radio.RadioStationNotProvidedPlaybackDataException
import pl.jwizard.jwl.command.Command

@JdaCommand(Command.RADIO_INFO)
class RadioInfoCmd(
	private val radioPlaybackMappersCache: RadioPlaybackMappersCache,
	commandEnvironment: CommandEnvironmentBean,
) : RadioCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true
	override val shouldRadioPlaying = true

	override fun executeRadio(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse
	) {
		val radioStation = manager.state.radioStreamScheduler.radioStation

		val message = createRadioPlaybackEmbedMessage(context, manager)
			?: throw RadioStationNotProvidedPlaybackDataException(context, radioStation)

		val refreshableComponent = createRefreshable {
			createRadioPlaybackEmbedMessage(context, manager)?.let { message -> it.add(message) }
		}
		refreshableComponent.initEvent()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(refreshableComponent.createRefreshButtonRow(context))
			.build()

		response.complete(commandResponse)
	}

	private fun createRadioPlaybackEmbedMessage(
		context: GuildCommandContext,
		manager: GuildMusicManager,
	): MessageEmbed? {
		val radioStation = manager.state.radioStreamScheduler.radioStation
		val mapper = radioPlaybackMappersCache
			.getCachedByProvider(radioStation.streamProvider.playbackProvider) ?: return null

		return mapper.createPlaybackDataMessage(radioStation, context)
	}
}
