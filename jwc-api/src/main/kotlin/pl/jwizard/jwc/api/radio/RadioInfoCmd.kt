/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api.radio

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import pl.jwizard.jwc.api.CommandEnvironmentBean
import pl.jwizard.jwc.api.RadioCommandBase
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.context.CommandContext
import pl.jwizard.jwc.command.interaction.component.RefreshableContent
import pl.jwizard.jwc.command.reflect.JdaCommand
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.radio.spi.RadioPlaybackMappersCache
import pl.jwizard.jwc.exception.radio.RadioStationNotProvidedPlaybackDataException
import pl.jwizard.jwl.command.Command

/**
 * Command to display information about the currently playing radio station.
 *
 * This command fetches and displays the current radio station's information, such as its details and playback data.
 * If there is no valid radio station data, it throws an appropriate exception.
 *
 * @param radioPlaybackMappersCache Cached radio playback mappers access.
 * @param commandEnvironment The environment context containing necessary information for executing the command.
 * @author Miłosz Gilga
 */
@JdaCommand(Command.RADIOINFO)
class RadioInfoCmd(
	private val radioPlaybackMappersCache: RadioPlaybackMappersCache,
	commandEnvironment: CommandEnvironmentBean,
) : RadioCommandBase(commandEnvironment), RefreshableContent<Pair<CommandContext, GuildMusicManager>> {

	override val shouldOnSameChannelWithBot = true
	override val shouldRadioPlaying = true

	/**
	 * Executes the command to retrieve and display the current radio station information.
	 *
	 * This method fetches the radio station's slug and attempts to retrieve detailed playback data. If successful, it
	 * builds a response containing the station's information and adds refreshable content.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The guild music manager responsible for handling the audio playback and stream management.
	 * @param response The future response object used to send the result of the command execution.
	 */
	override fun executeRadio(context: CommandContext, manager: GuildMusicManager, response: TFutureResponse) {
		val radioStation = manager.state.radioStreamScheduler.radioStation
		val mapper = radioPlaybackMappersCache.getCachedByProvider(radioStation.streamProvider.playbackProvider)
			?: throw RadioStationNotProvidedPlaybackDataException(context, radioStation)

		val message = mapper.createPlaybackDataMessage(radioStation, context)
		val refreshableComponent = createRefreshable(this, Pair(context, manager))
		refreshableComponent.initEvent()

		val commandResponse = CommandResponse.Builder()
			.addEmbedMessages(message)
			.addActionRows(refreshableComponent.createRefreshButtonRow(context))
			.build()

		response.complete(commandResponse)
	}

	/**
	 * Handles the refresh interaction for the radio info display.
	 *
	 * When a user clicks the refresh button, this method fetches the updated radio station details and refreshes the
	 * embed message with the new playback data.
	 *
	 * @param event The button interaction event triggered by the user.
	 * @param response The list of message embeds that will be updated with new content.
	 * @param payload A pair containing the command context and the guild music manager responsible for radio playback.
	 */
	override fun onRefresh(
		event: ButtonInteractionEvent,
		response: MutableList<MessageEmbed>,
		payload: Pair<CommandContext, GuildMusicManager>,
	) {
		val (context, manager) = payload
		val radioStation = manager.state.radioStreamScheduler.radioStation

		val mapper = radioPlaybackMappersCache.getCachedByProvider(radioStation.streamProvider.playbackProvider) ?: return
		val message = mapper.createPlaybackDataMessage(radioStation, context)

		response.add(message)
	}
}
