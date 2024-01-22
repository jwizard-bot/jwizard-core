/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.PlayerManager
import pl.jwizard.core.bean.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = "repeatcls")
class ClearRepeatTrackCmd(
	botConfiguration: BotConfiguration,
	playerManager: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManager
) {
	override fun executeMusicCmd(event: CompoundCommandEvent) {
	}
}
