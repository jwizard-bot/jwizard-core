/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JWizardCoreEntrypoint

fun main(args: Array<String>) {
	runApplication<JWizardCoreEntrypoint>(*args)
}
