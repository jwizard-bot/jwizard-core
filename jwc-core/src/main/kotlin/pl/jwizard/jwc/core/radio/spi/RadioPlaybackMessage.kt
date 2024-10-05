/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.radio.spi

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.radio.RadioStationDetails

/**
 * Interface for creating playback data messages for radio stations.
 *
 * This interface defines a method to generate a [MessageEmbed] containing playback information  about a specific radio
 * station. Implementing classes should provide the logic to create a formatted message based on the provided details
 * and context.
 *
 * @author Miłosz Gilga
 */
interface RadioPlaybackMessage {

	/**
	 * Creates a playback data message based on the provided radio station details and command context.
	 *
	 * @param details The details of the radio station for which the playback message is created.
	 * @param context The command context that provides additional information for message formatting.
	 * @return A [MessageEmbed] containing the playback information for the specified radio station.
	 */
	fun createPlaybackDataMessage(details: RadioStationDetails, context: CommandBaseContext): MessageEmbed
}
