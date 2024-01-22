/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.misc

import pl.jwizard.core.bean.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = "help")
class HelpCmd(
	botConfiguration: BotConfiguration,
) : AbstractCompositeCmd(
	botConfiguration
) {
	override fun execute(event: CompoundCommandEvent) {
	}
}
