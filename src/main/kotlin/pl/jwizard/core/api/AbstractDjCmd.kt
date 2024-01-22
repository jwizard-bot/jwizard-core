/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.audio.PlayerManager
import pl.jwizard.core.bean.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent

abstract class AbstractDjCmd(
	botConfiguration: BotConfiguration,
	playerManager: PlayerManager,
) : AbstractMusicCmd(
	botConfiguration,
	playerManager,
) {
	override fun executeMusicCmd(event: CompoundCommandEvent) {
		executeDjCmd(event)
	}

	abstract fun executeDjCmd(event: CompoundCommandEvent)
}
