package pl.jwizard.jwc.api.music

import pl.jwizard.jwac.player.track.Track
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.GuildCommandContext
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.audio.ActiveAudioPlayingNotFoundException
import pl.jwizard.jwl.command.Command

@JdaCommand(Command.PLAYING)
class CurrentPlayingCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {

	override val shouldPlayingMode = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse
	) {
		val playingTrack = manager.cachedPlayer?.track
			?: throw ActiveAudioPlayingNotFoundException(context)

		val refreshableComponent = createRefreshable {
			// run refresh event only if track is playing, when track is paused, ignore
			if (manager.cachedPlayer?.paused == false) {
				manager.cachedPlayer?.track?.let { track ->
					it.add(createEmbedMessage(context, manager, track))
				}
			}
		}
		refreshableComponent.initEvent()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(createEmbedMessage(context, manager, playingTrack))
			.addActionRows(refreshableComponent.createRefreshButtonRow(context))
			.build()

		response.complete(commandResponse)
	}

	private fun createEmbedMessage(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		playingTrack: Track,
	) = createDetailedTrackMessage(
		context,
		manager,
		i18nTitle = I18nAudioSource.CURRENT_PLAYING_TRACK,
		i18nPosition = I18nAudioSource.CURRENT_PLAYING_TIMESTAMP,
		track = playingTrack,
	)
}
