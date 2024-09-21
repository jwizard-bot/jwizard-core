/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.event

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow

/**
 * Represents the response from a command execution, including any embedded messages and action rows to be sent in the
 * Discord channel.
 *
 * @property embedMessages A list of embedded messages to be included in the response.
 * @property actionRows A list of action rows containing interactive components.
 * @author Miłosz Gilga
 */
data class CommandResponse(
	val embedMessages: List<MessageEmbed> = emptyList(),
	val actionRows: List<ActionRow> = emptyList(),
) {

	/**
	 * Secondary constructor for creating a CommandResponse with a single embed message and action row.
	 *
	 * @param embedMessage The embed message to include in the response.
	 * @param actionRow The action row to include in the response.
	 */
	constructor(embedMessage: MessageEmbed, actionRow: ActionRow) : this(listOf(embedMessage), listOf(actionRow))
}
