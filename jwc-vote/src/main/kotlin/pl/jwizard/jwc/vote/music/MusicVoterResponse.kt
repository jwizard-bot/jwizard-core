/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote.music

/**
 * A data class that represents a response for a music voting process.
 *
 * This class encapsulates the payload associated with the music voting response along with optional arguments that can
 * be utilized for additional context or dynamic content.
 *
 * @param T The type of payload associated with the music voting process.
 * @property payload The data or content related to the music vote, of type [T].
 * @property args A map of additional arguments that can be used to provide context or dynamic content. This is optional
 *           and defaults to an empty map.
 * @author Miłosz Gilga
 */
data class MusicVoterResponse<T : Any>(
    val payload: T,
    val args: Map<String, Any?> = emptyMap(),
)
