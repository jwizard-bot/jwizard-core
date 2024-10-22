/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.color

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.util.logger
import java.awt.Color

/**
 * Component responsible for managing and retrieving colors for JDA.
 *
 * @property environmentBean Provides access to application environment properties.
 * @author Miłosz Gilga
 */
@Component
class JdaColorStoreBean(private val environmentBean: EnvironmentBean) {

	companion object {
		private val log = logger<JdaColorStoreBean>()

		/**
		 * Default color value (black) used when a specific color is not found.
		 */
		private const val DEFAULT_COLOR = 0x000000
	}

	/**
	 * Map storing the colors associated with JDA color keys.
	 */
	private val colors = mutableMapOf<JdaColor, Int>()

	/**
	 * Loads colors from the environment properties and populates the colors map.
	 * Logs the number of colors loaded and their values.
	 */
	fun loadColors() {
		colors.putAll(JdaColor.entries.associateWith { environmentBean.getProperty<Int>(it.botProperty) })
		val loadedColors = colors.map { (key, value) -> "$key: ${"#%06X".format(value)}" }
		log.info("Load: {} colors: {}.", loadedColors.size, loadedColors)
	}

	/**
	 * Retrieves the hexadecimal color value for the specified [JdaColor]. If the color is not found, returns the default
	 * color (black).
	 *
	 * @param jdaColor The JdaColor enum for which to retrieve the color.
	 * @return A Color object representing the hex color.
	 */
	fun getHexColor(jdaColor: JdaColor) = Color(colors[jdaColor] ?: DEFAULT_COLOR)
}
