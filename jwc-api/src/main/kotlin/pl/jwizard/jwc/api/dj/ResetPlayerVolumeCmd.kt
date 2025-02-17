package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironment
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOLUME_CLEAR)
internal class ResetPlayerVolumeCmd(
	commandEnvironment: CommandEnvironment,
) : DjCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<ResetPlayerVolumeCmd>()
	}

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val defaultVolume = guildEnvironment
			.getGuildProperty<Int>(GuildProperty.PLAYER_VOLUME, context.guild.idLong)

		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setVolume(defaultVolume),
			onSuccess = {
				log.jdaInfo(context, "Reset volume to default value: %d%%.", defaultVolume)
				createEmbedMessage(context)
					.setDescription(
						i18nLocaleSource = I18nResponseSource.RESET_AUDIO_PLAYER_VOLUME,
						args = mapOf("defVolume" to defaultVolume),
					)
					.setColor(JdaColor.PRIMARY)
					.build()
			}
		)
	}
}
