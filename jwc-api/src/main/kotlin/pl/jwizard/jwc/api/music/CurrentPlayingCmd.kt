/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.music

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import pl.jwizard.jwc.api.MusicCommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.RefreshableComponent
import pl.jwizard.jwc.command.interaction.component.RefreshableContent
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.audio.spi.MusicManager
import pl.jwizard.jwc.core.i18n.source.I18nAudioSource
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.exception.audio.ActiveAudioPlayingNotFoundException
import pl.jwizard.jwl.command.Command

/**
 * Command for showing the currently playing track in the music queue.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.PLAYING)
class CurrentPlayingCmd(
	commandEnvironment: CommandEnvironmentBean,
) : MusicCommandBase(commandEnvironment), RefreshableContent<Pair<CommandContext, MusicManager>> {

	override val shouldPlayingMode = true

	/**
	 * Executes the command to retrieve and display the currently playing track.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 * @throws ActiveAudioPlayingNotFoundException If no track is currently playing.
	 */
	override fun executeMusic(context: CommandContext, manager: MusicManager, response: TFutureResponse) {
		val playingTrack = manager.cachedPlayer?.track ?: throw ActiveAudioPlayingNotFoundException(context)

		val message = createDetailedTrackMessage(
			context,
			manager,
			i18nTitle = I18nAudioSource.CURRENT_PLAYING_TRACK,
			i18nPosition = I18nAudioSource.CURRENT_PLAYING_TIMESTAMP,
			track = playingTrack,
		)
		val refreshableComponent = RefreshableComponent(i18nBean, eventQueueBean, this, Pair(context, manager))
		refreshableComponent.initEvent()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(refreshableComponent.createRefreshButtonRow(context))
			.build()

		response.complete(commandResponse)
	}

	/**
	 * Updates the displayed information about the currently playing track when the refresh button is clicked.
	 *
	 * @param event The button interaction event that triggered the refresh.
	 * @param response The list of message embeds that will be sent as part of the response.
	 * @param payload A pair containing the command context and the music manager instance used to retrieve track details.
	 */
	override fun onRefresh(
		event: ButtonInteractionEvent,
		response: MutableList<MessageEmbed>,
		payload: Pair<CommandContext, MusicManager>,
	) {
		val (context, manager) = payload
		val playingTrack = manager.cachedPlayer?.track ?: return
		val message = createDetailedTrackMessage(
			context,
			manager,
			i18nTitle = I18nAudioSource.CURRENT_PLAYING_TRACK,
			i18nPosition = I18nAudioSource.CURRENT_PLAYING_TIMESTAMP,
			playingTrack,
		)
		response.add(message)
	}
}
