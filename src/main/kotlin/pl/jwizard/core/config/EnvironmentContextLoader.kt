/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.config

import io.github.cdimascio.dotenv.Dotenv
import pl.jwizard.core.log.AbstractLoggingBean
import java.io.File

object EnvironmentContextLoader : AbstractLoggingBean(EnvironmentContextLoader::class) {
	fun loadContext() {
		val profile = System.getProperty("spring.profiles.active")
		if (profile != "prod") {
			return
		}
		val envFileKeys = getEnvFileKeys()
		if (envFileKeys.isNotEmpty()) {
			val dotenv = Dotenv.load()
			envFileKeys.forEach { System.setProperty(it, dotenv.get(it)) }
			log.info("Found {} environment keys: {}", envFileKeys.size, envFileKeys)
		}
	}

	private fun getEnvFileKeys(): Array<String> {
		val file = File(".env")
		if (!file.exists()) {
			return emptyArray()
		}
		return file.readLines()
			.filter { line -> line.isNotBlank() && !line.startsWith("#") }
			.mapNotNull { line ->
				val key = line.split("=", limit = 2).firstOrNull()?.trim()
				if (key.isNullOrEmpty()) null else key
			}.toTypedArray()
	}
}
