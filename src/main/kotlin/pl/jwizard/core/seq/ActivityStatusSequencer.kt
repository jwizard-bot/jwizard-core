/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.seq

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import org.springframework.stereotype.Component
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.bot.properties.BotProperties
import pl.jwizard.core.log.AbstractLoggingBean
import java.util.concurrent.TimeUnit

@Component
class ActivityStatusSequencer(
	private val botProperties: BotProperties,
	private val botConfiguration: BotConfiguration,
) : AbstractLoggingBean(ActivityStatusSequencer::class) {

	private var position = 0
	private final var splashes: List<String> = emptyList()

	fun loadSplashes() {
		splashes = botProperties.splashes.list
		log.info("Successfully loaded {} splashes: {}", splashes.size, splashes)
	}

	fun initFixedDelay(jda: JDA) {
		if (!botProperties.splashes.enabled) {
			jda.presence.activity = Activity.listening(botProperties.defaultActivity)
			return
		}
		botConfiguration.threadPool.scheduleWithFixedDelay({
			val activity = botProperties.splashes.list[position]
			jda.presence.activity = Activity.listening(activity)
			position = (position + 1) % botProperties.splashes.list.size
		}, 0, botProperties.splashes.intervalSec, TimeUnit.SECONDS)
	}
}
