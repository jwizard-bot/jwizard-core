package pl.jwizard.jwc.radio

import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.radio.PlaybackProvider

@SingletonComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RadioPlaybackMapper(
	val value: PlaybackProvider,
)
