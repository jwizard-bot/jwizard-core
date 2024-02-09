/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.playlist

import pl.jwizard.core.api.AbstractPlaylistCmd
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = BotCommand.ADDPLAYLIST)
class CreatePlaylistCmd(
	botConfiguration: BotConfiguration,
) : AbstractPlaylistCmd(
	botConfiguration
) {
	override fun executePlaylistCmd(event: CompoundCommandEvent) {
	}
}
