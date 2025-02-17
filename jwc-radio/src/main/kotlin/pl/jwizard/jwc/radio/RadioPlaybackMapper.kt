package pl.jwizard.jwc.radio

import org.springframework.stereotype.Component
import pl.jwizard.jwl.radio.PlaybackProvider

@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class RadioPlaybackMapper(
	val value: PlaybackProvider,
)
