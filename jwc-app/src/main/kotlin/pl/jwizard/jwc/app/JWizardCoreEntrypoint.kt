package pl.jwizard.jwc.app

import pl.jwizard.jwl.AppRunner
import pl.jwizard.jwl.ioc.AppContextInitiator

@AppContextInitiator
class JWizardCoreEntrypoint

fun main() {
	AppRunner.run(JWizardCoreEntrypoint::class)
}
