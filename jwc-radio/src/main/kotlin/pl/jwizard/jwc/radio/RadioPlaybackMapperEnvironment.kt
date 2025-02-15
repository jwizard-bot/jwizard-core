package pl.jwizard.jwc.radio

import com.fasterxml.jackson.databind.ObjectMapper
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
internal class RadioPlaybackMapperEnvironment(
	val i18nBean: I18nBean,
	val jdaColorsCache: JdaColorsCacheBean,
	val objectMapper: ObjectMapper,
	val environmentBean: EnvironmentBean,
)
