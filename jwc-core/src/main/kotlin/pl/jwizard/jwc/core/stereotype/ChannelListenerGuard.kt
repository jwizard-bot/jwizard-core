/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.stereotype

import net.dv8tion.jda.api.JDA

/**
 * TODO
 *
 * @author Miłosz Gilga
 */
interface ChannelListenerGuard {

	/**
	 * TODO
	 *
	 * @param jda
	 */
	fun initThreadPool(jda: JDA)

	/**
	 * TODO
	 *
	 */
	fun onEveryVoiceUpdate()
}
