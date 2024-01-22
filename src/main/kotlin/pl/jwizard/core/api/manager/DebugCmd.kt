/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.manager

import pl.jwizard.core.api.AbstractManagerCmd
import pl.jwizard.core.bean.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = "debug")
class DebugCmd(
	botConfiguration: BotConfiguration,
) : AbstractManagerCmd(
	botConfiguration
) {
	override fun executeManagerCmd(event: CompoundCommandEvent) {
	}
}
