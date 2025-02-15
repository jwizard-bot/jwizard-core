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
import pl.jwizard.jwl.command.Command

@JdaCommand(Command.VOLUME)
class GetPlayerVolumeCmd(commandEnvironment: CommandEnvironmentBean) : MusicCommandBase(commandEnvironment) {

	override val shouldOnSameChannelWithBot = true
	override val shouldPlayingMode = true

	override fun executeMusic(context: GuildCommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val message = createEmbedMessage(context)
			.setDescription(
				i18nLocaleSource = I18nResponseSource.GET_CURRENT_AUDIO_PLAYER_VOLUME,
				args = mapOf("currentVolume" to manager.cachedPlayer?.volume)
			)
			.setColor(JdaColor.PRIMARY)
			.build()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()

		response.complete(commandResponse)
	}
}
