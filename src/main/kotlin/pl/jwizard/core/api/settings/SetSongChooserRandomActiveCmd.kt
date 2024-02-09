/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.settings

import pl.jwizard.core.api.AbstractSettingsCmd
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = BotCommand.SETRCHOSSNG)
class SetSongChooserRandomActiveCmd(
	botConfiguration: BotConfiguration,
) : AbstractSettingsCmd(
	botConfiguration
) {
	override fun executeSettingsCmd(event: CompoundCommandEvent) {
	}
}
