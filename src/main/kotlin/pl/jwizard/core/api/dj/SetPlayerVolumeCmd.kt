/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.dj

import pl.jwizard.core.api.AbstractDjCmd
import pl.jwizard.core.audio.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = "setvolume")
class SetPlayerVolumeCmd(
	botConfiguration: BotConfiguration,
	playerManager: PlayerManager
) : AbstractDjCmd(
	botConfiguration,
	playerManager
) {
	override fun executeDjCmd(event: CompoundCommandEvent) {
	}
}
