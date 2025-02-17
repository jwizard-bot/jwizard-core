package pl.jwizard.jwc.core.jda.color

import org.springframework.stereotype.Component
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.util.logger
import java.awt.Color

@Component
class JdaColorsCache(private val environment: BaseEnvironment) {
	companion object {
		private val log = logger<JdaColorsCache>()

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
