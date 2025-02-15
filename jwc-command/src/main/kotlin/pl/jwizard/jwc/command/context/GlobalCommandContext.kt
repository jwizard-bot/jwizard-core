package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

class GlobalCommandContext(
	override val commandName: String,
	event: SlashCommandInteractionEvent,
) : ArgumentContext() {
	override val language = event.userLocale.locale

	override val prefix = "/"

	override val suppressResponseNotifications = false

	override val isSlashEvent = true
}
