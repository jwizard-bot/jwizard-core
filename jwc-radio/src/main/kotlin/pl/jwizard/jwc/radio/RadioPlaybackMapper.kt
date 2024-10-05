/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.radio

import org.springframework.stereotype.Component

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
@Component
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RadioPlaybackMapper
