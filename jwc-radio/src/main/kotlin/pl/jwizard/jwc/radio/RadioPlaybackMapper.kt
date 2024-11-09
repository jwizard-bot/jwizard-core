/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Annotation used to mark classes that handle radio playback mapping. These classes implement custom logic for parsing
 * radio playback data and should extend the [RadioPlaybackMapperHandler] class.
 *
 * ```kotlin
 * @RadioPlaybackFetcher
 * class CustomRadioPlaybackMapper : RadioPlaybackMapperHandler() {
 *   fun parsePlaybackData(responseRaw: String, details: RadioStationDetails): RadioPlaybackResponse {
 *     // parse raw string response to object
 *   }
 * }
 * ```
 *
 * @author Miłosz Gilga
 * @see RadioPlaybackMapperHandler
 * @see RadioPlaybackResponse
 */
@SingletonComponent
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RadioPlaybackMapper
