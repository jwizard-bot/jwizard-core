/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import pl.jwizard.core.bot.BotInstance
import pl.jwizard.core.config.EnvironmentContextLoader

@SpringBootApplication
class JWizardCoreEntrypoint(private val botInstance: BotInstance) : CommandLineRunner {
	override fun run(vararg args: String?) = botInstance.start()
}

fun main(args: Array<String>) {
	EnvironmentContextLoader.loadContext()
	runApplication<JWizardCoreEntrypoint>(*args)
}
