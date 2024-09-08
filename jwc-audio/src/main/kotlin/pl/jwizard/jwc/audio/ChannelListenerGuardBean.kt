/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.stereotype.ChannelListenerGuard

/**
 * TODO
 *
 * @author Miłosz Gilga
 */
@Component
class ChannelListenerGuardBean : ChannelListenerGuard {

	companion object {
		private val log = LoggerFactory.getLogger(ChannelListenerGuardBean::class.java)
	}

	override fun initThreadPool(jda: JDA) {
		log.info("init thread pool")
	}

	/**
	 * TODO
	 *
	 */
	override fun onEveryVoiceUpdate() {
		log.info("On every voice update")
	}
}
