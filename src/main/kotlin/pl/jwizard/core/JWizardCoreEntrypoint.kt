/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JWizardCoreEntrypoint(private val jdaBotInstance: JdaBotInstance) : CommandLineRunner {
	override fun run(vararg args: String?) = jdaBotInstance.start()
}

fun main(args: Array<String>) {
	runApplication<JWizardCoreEntrypoint>(*args)
}
