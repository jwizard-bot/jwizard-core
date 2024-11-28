/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda

import pl.jwizard.jwc.core.jvm.thread.JvmFixedThreadExecutor
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger

/**
 * Manage and update the activity status (or "splash") of a JDA (Java Discord API) bot.
 * This component handles rotating through a list of splash texts or statuses at a specified interval.
 *
 * @property environment Provide access to environment properties, used to configure splash settings and intervals.
 * @property jdaShardManager Manages multiple shards of the JDA bot, responsible for handling Discord API interactions.
 * @author Miłosz Gilga
 * @see JvmFixedThreadExecutor
 */
@SingletonComponent
class ActivitySplashesBean(
	private val environment: EnvironmentBean,
	private val jdaShardManager: JdaShardManagerBean,
) : JvmFixedThreadExecutor() {

	companion object {
		private val log = logger<ActivitySplashesBean>()
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
		val splashesEnabled = environment.getProperty<Boolean>(BotProperty.JDA_SPLASHES_ENABLED)
		splashes = environment.getListProperty<String>(BotListProperty.JDA_SPLASHES_ELEMENTS)

		if (!splashesEnabled || splashes.isEmpty()) {
			val defaultActivity = environment.getProperty<String>(BotProperty.JDA_DEFAULT_ACTIVITY)
			jdaShardManager.setPresenceActivity(defaultActivity)
			return
		}
		log.info("Start single thread executor service for activity splashes: {}", splashes)
		start(intervalSec = environment.getProperty<Long>(BotProperty.JDA_SPLASHES_INTERVAL_SEC))
	}

	/**
	 * Runs the splash update logic at the specified interval.
	 * Updates the JDA bot's activity status to the current splash text and rotates to the next one.
	 * The splash text list is cycled through, so after reaching the end, it starts again from the beginning.
	 */
	override fun executeJvmThread() {
		jdaShardManager.setPresenceActivity(splashes[position])
		position = (position + 1) % splashes.size
	}
}
