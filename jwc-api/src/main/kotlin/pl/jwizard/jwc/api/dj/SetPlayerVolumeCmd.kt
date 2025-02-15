package pl.jwizard.jwc.api.dj

import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.DjCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.exception.audio.VolumeUnitsOutOfBoundsException
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.util.logger

@JdaCommand(Command.VOLUME_SET)
class SetPlayerVolumeCmd(
	commandEnvironment: CommandEnvironmentBean,
) : DjCommandBase(commandEnvironment) {
	companion object {
		private val log = logger<SetPlayerVolumeCmd>()
	}

	override fun executeDj(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		val newVolume = context.getArg<Int>(Argument.VOLUME)
		val maxVolume = environment.getProperty<Int>(AppBaseProperty.PLAYER_MAX_VOLUME)

		val currentVolume = manager.cachedPlayer?.volume
		if (newVolume < 0 || newVolume > maxVolume) {
			throw VolumeUnitsOutOfBoundsException(context, newVolume, 0, maxVolume)
		}
		val asyncUpdatableHandler = createAsyncUpdatablePlayerHandler(context, response)
		asyncUpdatableHandler.performAsyncUpdate(
			asyncAction = manager.createdOrUpdatedPlayer.setVolume(newVolume),
			onSuccess = {
				val afterChangedVolume = manager.cachedPlayer?.volume
				log.jdaInfo(
					context,
					"Changed volume from: %d%% to: %d%%.",
					currentVolume,
					afterChangedVolume
				)
				createEmbedMessage(context)
					.setDescription(
						i18nLocaleSource = I18nResponseSource.SET_CURRENT_AUDIO_PLAYER_VOLUME,
						args = mapOf(
							"previousVolume" to currentVolume,
							"setVolume" to afterChangedVolume,
						),
					)
					.setColor(JdaColor.PRIMARY)
					.build()
			},
		)
	}
}
