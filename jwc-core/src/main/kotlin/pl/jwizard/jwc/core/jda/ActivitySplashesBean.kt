/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.spi.JdaInstance
import pl.jwizard.jwc.core.jvm.JvmThreadExecutor
import pl.jwizard.jwc.core.property.BotMultiProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * Manage and update the activity status (or "splash") of a JDA (Java Discord API) bot.
 * This component handles rotating through a list of splash texts or statuses at a specified interval.
 *
 * @property environmentBean Provide access to environment properties, used to configure splash settings and intervals.
 * @property jdaInstanceBean Provide access to the JDA instance, used to set the bot's activity status.
 * @author Miłosz Gilga
 * @see JvmThreadExecutor
 */
@Component
class ActivitySplashesBean(
	private val environmentBean: EnvironmentBean,
	private val jdaInstanceBean: JdaInstance,
) : JvmThreadExecutor() {

	companion object {
		private val log = LoggerFactory.getLogger(ActivitySplashesBean::class.java)
	}

	/**
	 * The list of splash texts or statuses to be displayed by the bot.
	 * Initialized as an empty list and populated when the splash sequence is initialized in [initSplashesSequence].
	 */
	private var splashes = emptyList<String>()

	/**
	 * The current index position in the list of splash texts.
	 * Used to determine which splash text should be displayed next.
	 */
	private var position = 0

	/**
	 * Initializes the splash sequence by loading splash texts and the interval from the environment properties.
	 * If splash texts are not enabled or the list is empty, sets the default activity status.
	 * Starts the thread executor service to rotate through splash texts at the specified interval.
	 */
	fun initSplashesSequence() {
		val splashesEnabled = environmentBean.getProperty<Boolean>(BotProperty.JDA_SPLASHES_ENABLED)
		splashes = environmentBean.getMultiProperty<String>(BotMultiProperty.JDA_SPLASHES_ELEMENTS)

		if (!splashesEnabled || splashes.isEmpty()) {
			val defaultActivity = environmentBean.getProperty<String>(BotProperty.JDA_DEFAULT_ACTIVITY)
			jdaInstanceBean.setPresenceActivity(defaultActivity)
			return
		}
		log.info("Start single thread executor service for activity splashes: {}", splashes)
		start(intervalSec = environmentBean.getProperty<Long>(BotProperty.JDA_SPLASHES_INTERVAL_SEC))
	}

	/**
	 * Runs the splash update logic at the specified interval.
	 * Updates the JDA bot's activity status to the current splash text and rotates to the next one.
	 * The splash text list is cycled through, so after reaching the end, it starts again from the beginning.
	 */
	override fun executeJvmThread() {
		jdaInstanceBean.setPresenceActivity(splashes[position])
		position = (position + 1) % splashes.size
	}
}
