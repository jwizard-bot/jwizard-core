/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core

import pl.jwizard.core.bean.BotInstance
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JWizardCoreEntrypoint(private val botInstance: BotInstance) : CommandLineRunner {
	override fun run(vararg args: String?) = botInstance.start()
}

fun main(args: Array<String>) {
	runApplication<JWizardCoreEntrypoint>(*args)
}
