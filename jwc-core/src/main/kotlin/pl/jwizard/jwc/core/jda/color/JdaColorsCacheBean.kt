package pl.jwizard.jwc.core.jda.color

import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.util.logger
import java.awt.Color

@SingletonComponent
class JdaColorsCacheBean(private val environment: EnvironmentBean) {
	companion object {
		private val log = logger<JdaColorsCacheBean>()

		// selected if color not found
		private const val DEFAULT_COLOR = "#000000"
	}

	private val colors = mutableMapOf<JdaColor, String>()

	fun loadColors() {
		colors.putAll(JdaColor.entries.associateWith {
			environment.getProperty(it.botProperty)
		})
		log.info("Load: {} colors: {}.", colors.size, colors)
	}

	fun getHexColor(jdaColor: JdaColor): Color = Color.decode(colors[jdaColor] ?: DEFAULT_COLOR)
}
