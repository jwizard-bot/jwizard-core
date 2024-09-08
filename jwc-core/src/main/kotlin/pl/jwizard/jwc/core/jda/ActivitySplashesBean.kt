/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda

import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * TODO
 *
 * @author Miłosz Gilga
 */
@Component
class ActivitySplashesBean {

	companion object {
		private val log = LoggerFactory.getLogger(ActivitySplashesBean::class.java)
	}

	/**
	 * TODO
	 *
	 */
	fun initSplashesSequence(jda: JDA) {
		log.info("init splashes sequence")
	}
}
