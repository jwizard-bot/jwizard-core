/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.exception

import pl.jwizard.core.command.CompoundCommandEvent

object UtilException {
	class UnexpectedException(cause: String) : AbstractBotException(
		UnexpectedException::class,
		i18nLocale = I18nExceptionLocale.UNEXPECTED_EXCEPTION,
		logMessage = "Unexpected bot exception. Cause: $cause"
	)

	class ModuleIsTurnedOffException(event: CompoundCommandEvent, moduleName: String) : AbstractBotException(
		event, ModuleIsTurnedOffException::class,
		i18nLocale = I18nExceptionLocale.MODULE_IS_TURNED_OFF,
		variables = mapOf("moduleName" to moduleName),
		logMessage = "Attempt to invoke command from currently turned off module $moduleName"
	)
}
