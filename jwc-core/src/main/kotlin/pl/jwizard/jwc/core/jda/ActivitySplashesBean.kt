package pl.jwizard.jwc.core.jda

import pl.jwizard.jwc.core.jvm.thread.JvmFixedThreadExecutor
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger

@SingletonComponent
class ActivitySplashesBean(
	private val environment: EnvironmentBean,
	private val jdaShardManager: JdaShardManagerBean,
) : JvmFixedThreadExecutor() {
	companion object {
		private val log = logger<ActivitySplashesBean>()
	}

	private var splashes = emptyList<String>()
	private var position = 0

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

	override fun executeJvmThread() {
		jdaShardManager.setPresenceActivity(splashes[position])
		position = (position + 1) % splashes.size
	}
}
