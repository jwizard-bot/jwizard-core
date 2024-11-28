/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.app

import pl.jwizard.jwc.core.DiscordBotAppRunner
import pl.jwizard.jwl.AppContextInitiator

/**
 * Main application class.
 *
 * @author Miłosz Gilga
 */
@AppContextInitiator
class JWizardCoreEntrypoint

fun main() {
	DiscordBotAppRunner.run(JWizardCoreEntrypoint::class)
}
