/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.audio.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CompoundCommandEvent

abstract class AbstractMusicCmd(
	botConfiguration: BotConfiguration,
	protected val playerManager: PlayerManager,
) : AbstractCompositeCmd(
	botConfiguration
) {
	override fun execute(event: CompoundCommandEvent) {
		executeMusicCmd(event)
	}

	abstract fun executeMusicCmd(event: CompoundCommandEvent)
}
