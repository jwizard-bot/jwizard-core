package pl.jwizard.jwc.radio

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.property.BaseEnvironment

@Component
internal class RadioPlaybackMapperEnvironment(
	val i18n: I18n,
	val jdaColorsCache: JdaColorsCache,
	val objectMapper: ObjectMapper,
	val environment: BaseEnvironment,
)
