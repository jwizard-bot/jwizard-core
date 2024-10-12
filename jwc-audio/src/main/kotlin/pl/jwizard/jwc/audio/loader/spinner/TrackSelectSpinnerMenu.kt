/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.loader.spinner

import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.interaction.SelectSpinnerMenu
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.property.GuildMultipleProperties
import pl.jwizard.jwc.core.property.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.logger

/**
 * Manages the selection and processing of tracks from a spinner menu in a Discord guild. This class handles user
 * interaction with track selection, as well as automatic selection when a timeout occurs. It integrates with the music
 * manager to queue tracks and display responses.
 *
 * @property musicManager The guild's music manager to handle audio playback.
 * @property options The list of available track options for the user to choose from.
 * @property guildMultipleProperties Manages the properties related to track selection timing and limits.
 * @property action Provides actions for queuing tracks and creating response messages.
 * @author Miłosz Gilga
 */
class TrackSelectSpinnerMenu(
	private val musicManager: GuildMusicManager,
	private val options: List<TrackMenuOption>,
	private val guildMultipleProperties: GuildMultipleProperties,
	private val action: TrackSelectSpinnerAction,
) : SelectSpinnerMenu<TrackMenuOption>(musicManager.state.context, options) {

	companion object {
		private val log = logger<TrackSelectSpinnerMenu>()
	}

	/**
	 * Handles the event when the user selects a track from the spinner menu. The selected track is enqueued, and a
	 * response message is sent to the guild's text channel.
	 *
	 * @param event The event triggered by user interaction with the track selection menu.
	 * @param context The base command context containing information about the interaction.
	 * @param options The selected track options.
	 */
	override fun onEvent(
		event: StringSelectInteractionEvent,
		context: CommandBaseContext,
		options: List<TrackMenuOption>,
	) {
		val option = options[0]
		action.onEnqueueTrack(option.track)
		musicManager.sendMessage(action.createTrackResponseMessage((option.track)))
		log.jdaInfo(context, "Add track: %s after self-choose choice.", option.track.qualifier)
	}

	/**
	 * Handles the event when the track selection times out. Automatically selects and enqueues a track based on the
	 * provided timeout properties.
	 *
	 * @param context The base command context containing information about the interaction.
	 * @param option The track option chosen after timeout.
	 */
	override fun onTimeout(context: CommandBaseContext, option: TrackMenuOption) {
		action.onEnqueueTrack(option.track)
		musicManager.sendMessage(action.createTrackResponseMessage(option.track))
		log.jdaInfo(context, "Add track: %s after timeout: (%ds).", option.track.qualifier, elapsedTimeSec)
	}

	/**
	 * Provides the ID of the menu, used to identify the track selection menu.
	 */
	override val menuId
		get() = "track"

	/**
	 * Retrieves the time in seconds after which a track is automatically selected if the user doesn't choose.
	 */
	override val elapsedTimeSec
		get() = guildMultipleProperties.getProperty<Long>(GuildProperty.TIME_AFTER_AUTO_CHOOSE_SEC)

	/**
	 * Defines the maximum number of tracks that can be selected from the spinner menu.
	 */
	override val maxElementsToChoose
		get() = guildMultipleProperties.getProperty<Int>(GuildProperty.MAX_TRACKS_TO_CHOOSE)

	/**
	 * Specifies if the track should be chosen randomly after the timeout.
	 */
	override val randomChoice
		get() = guildMultipleProperties.getProperty<Boolean>(GuildProperty.RANDOM_AUTO_CHOOSE_TRACK)
}
