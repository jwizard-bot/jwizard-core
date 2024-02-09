/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CompoundCommandEvent

abstract class AbstractPlaylistPlayerCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade,
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade,
) {
	override fun executeMusicCmd(event: CompoundCommandEvent) {
		checkIfCommandModuleIsEnabled(event, CommandModule.PLAYLIST)
		executePlaylistPlayerCmd(event)
	}
	
	abstract fun executePlaylistPlayerCmd(event: CompoundCommandEvent)
}

