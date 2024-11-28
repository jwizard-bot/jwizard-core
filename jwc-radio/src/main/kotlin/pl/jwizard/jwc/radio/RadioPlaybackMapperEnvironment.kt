/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import com.fasterxml.jackson.databind.ObjectMapper
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Represents the environment for radio playback mappers, providing necessary dependencies
 * such as internationalization support, color store, and JSON object mapper.
 *
 * @property i18nBean The bean for managing internationalization (i18n) messages and translations.
 * @property jdaColorsCache The bean responsible for storing and managing color data in the JDA (Java Discord API)
 *           context.
 * @property objectMapper The Jackson ObjectMapper used for converting objects to and from JSON.
 * @property environmentBean Provides access to application environment properties.
 * @author Miłosz Gilga
 */
@SingletonComponent
class RadioPlaybackMapperEnvironment(
	val i18nBean: I18nBean,
	val jdaColorsCache: JdaColorsCacheBean,
	val objectMapper: ObjectMapper,
	val environmentBean: EnvironmentBean,
)
