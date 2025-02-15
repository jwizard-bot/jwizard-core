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
import pl.jwizard.jwc.exception.UnexpectedException
import pl.jwizard.jwl.command.Command

@JdaCommand(Command.PAUSED)
class CurrentPausedCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment) {

	override val shouldPaused = true

	override fun executeMusic(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		response: TFutureResponse,
	) {
		// should never have thrown this exception, but who knows
		val pausedTrack = manager.cachedPlayer?.track
			?: throw UnexpectedException(context, "Paused track is NULL.")

		val refreshableComponent = createRefreshable {
			// run refresh event only if track is paused, when track is playing, ignore
			if (manager.cachedPlayer?.paused == true) {
				manager.cachedPlayer?.track?.let { track ->
					it.add(createDetailedTrackMessage(context, manager, track))
				}
			}
		}
		refreshableComponent.initEvent()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(createDetailedTrackMessage(context, manager, pausedTrack))
			.addActionRows(refreshableComponent.createRefreshButtonRow(context))
			.build()

		response.complete(commandResponse)
	}

	private fun createDetailedTrackMessage(
		context: GuildCommandContext,
		manager: GuildMusicManager,
		pausedTrack: Track,
	) = createDetailedTrackMessage(
		context,
		manager,
		i18nTitle = I18nAudioSource.CURRENT_PAUSED_TRACK,
		i18nPosition = I18nAudioSource.CURRENT_PAUSED_TIMESTAMP,
		track = pausedTrack,
	)
}
