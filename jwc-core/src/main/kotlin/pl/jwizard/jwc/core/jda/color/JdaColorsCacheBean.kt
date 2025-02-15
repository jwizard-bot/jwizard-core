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
		private const val DEFAULT_COLOR = 0x000000
	}

	private val colors = mutableMapOf<JdaColor, Int>()

	fun loadColors() {
		colors.putAll(JdaColor.entries.associateWith {
			Integer.decode(environment.getProperty(it.botProperty))
		})
		val loadedColors = colors.map { (key, value) -> "$key: ${"#%06X".format(value)}" }
		log.info("Load: {} colors: {}.", loadedColors.size, loadedColors)
	}

	fun getHexColor(jdaColor: JdaColor) = Color(colors[jdaColor] ?: DEFAULT_COLOR)
}
