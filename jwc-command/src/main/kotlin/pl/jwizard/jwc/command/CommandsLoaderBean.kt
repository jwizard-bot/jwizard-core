/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.CommandsLoader

/**
 * TODO
 *
 * @author Miłosz Gilga
 */
@Component
class CommandsLoaderBean : CommandsLoader {

	companion object {
		private val log = LoggerFactory.getLogger(CommandsLoaderBean::class.java)
	}

	/**
	 * TODO
	 *
	 */
	override fun loadMetadata() {
		log.info("load metadata")
	}

	/**
	 * TODO
	 *
	 */
	override fun checkIntegrity() {
		log.info("check integrity")
	}

	/**
	 * TODO
	 *
	 */
	override fun loadClassesViaReflectionApi() {
		log.info("load classes via reflection api")
	}
}
