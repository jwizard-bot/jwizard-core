/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.util.BotUtils

abstract class AbstractDjCmd(
	botConfiguration: BotConfiguration,
	playerManager: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManager,
) {
	protected var allowAlsoForNormal = true

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		val validatedUserDetails = BotUtils.validateUserDetails(event)
		val typicalRightsMember = validatedUserDetails.concatPositive()
		val actions = playerManager.findMusicManager(event).actions
		if (allowAlsoForNormal) {
			val allTracksFromOneMember = actions.checkIfAllTracksIsFromSelectedMember(event.member)
			if (!allTracksFromOneMember && typicalRightsMember) {
				throw UserException.UnauthorizedDjOrSenderException(event)
			}
		} else if (typicalRightsMember) {
			throw UserException.UnauthorizedDjException(event)
		}
		executeDjCmd(event)
	}

	abstract fun executeDjCmd(event: CompoundCommandEvent)
}
