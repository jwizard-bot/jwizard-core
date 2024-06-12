/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.util.BotUtils

abstract class AbstractManagerCmd(
	botConfiguration: BotConfiguration,
) : AbstractCompositeCmd(
	botConfiguration
) {
	override fun execute(event: CompoundCommandEvent) {
		val (isNotOwner, isNotManager) = BotUtils.validateUserDetails(event)
		if (isNotOwner && isNotManager) {
			throw UserException.UnauthorizedManagerException(event)
		}
		executeManagerCmd(event)
	}

	abstract fun executeManagerCmd(event: CompoundCommandEvent)
}
