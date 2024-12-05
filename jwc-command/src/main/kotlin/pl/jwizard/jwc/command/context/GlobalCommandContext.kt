/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

/**
 * Class representing the context of a global slash command execution.
 *
 * @property event The [SlashCommandInteractionEvent] that represents the slash command event triggered by the user.
 * @property commandName The name of the incoming command that was triggered.
 * @author Miłosz Gilga
 */
class GlobalCommandContext(
	private val event: SlashCommandInteractionEvent,
	override val commandName: String,
) : ArgumentContext() {

	override val language = event.userLocale.locale
	override val prefix = "/"
	override val suppressResponseNotifications = false
	override val isSlashEvent = true
}
