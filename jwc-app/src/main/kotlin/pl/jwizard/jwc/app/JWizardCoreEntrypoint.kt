package pl.jwizard.jwc.app

import pl.jwizard.jwc.core.DiscordBotAppRunner
import pl.jwizard.jwl.ioc.AppContextInitiator

@AppContextInitiator
class JWizardCoreEntrypoint

fun main() {
	DiscordBotAppRunner.run(JWizardCoreEntrypoint::class)
}
