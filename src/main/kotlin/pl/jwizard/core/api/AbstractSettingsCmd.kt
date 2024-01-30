/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent

abstract class AbstractSettingsCmd(
	botConfiguration: BotConfiguration,
) : AbstractManagerCmd(
	botConfiguration,
) {
	override fun executeManagerCmd(event: CompoundCommandEvent) {
		executeSettingsCmd(event)
	}

	abstract fun executeSettingsCmd(event: CompoundCommandEvent)
}
