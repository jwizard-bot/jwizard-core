/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CompoundCommandEvent

abstract class AbstractPlaylistCmd(
	botConfiguration: BotConfiguration,
) : AbstractCompositeCmd(
	botConfiguration
) {
	override fun execute(event: CompoundCommandEvent) {
		checkIfCommandModuleIsEnabled(event, CommandModule.PLAYLIST)
		executePlaylistCmd(event)
	}

	abstract fun executePlaylistCmd(event: CompoundCommandEvent)
}
