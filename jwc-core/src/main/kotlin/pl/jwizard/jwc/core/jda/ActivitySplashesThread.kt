package pl.jwizard.jwc.core.jda

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.thread.JvmFixedThreadExecutor
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.util.logger

@Component
internal class ActivitySplashesThread(
	private val environment: BaseEnvironment,
	private val jdaShardManager: JdaShardManager,
) : JvmFixedThreadExecutor() {
	companion object {
		private val log = logger<ActivitySplashesThread>()
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
