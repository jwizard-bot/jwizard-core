/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.vote

import pl.jwizard.core.api.AbstractVoteMusicCmd
import pl.jwizard.core.audio.PlayerManager
import pl.jwizard.core.bean.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = "vclear")
class VoteClearQueueCmd(
	botConfiguration: BotConfiguration,
	playerManager: PlayerManager
) : AbstractVoteMusicCmd(
	botConfiguration,
	playerManager
) {
	override fun executeVoteMusicCmd(event: CompoundCommandEvent) {
	}
}
