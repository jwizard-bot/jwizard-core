/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Data class representing an audio sender, typically used to encapsulate
 * the information about the user who initiated an audio-related action.
 *
 * @property authorId The unique identifier of the author (user) who sent the audio. This ID is typically used to
 *           associate audio actions with the specific user.
 * @author Miłosz Gilga
 */
data class AudioSender @JsonCreator constructor(@JsonProperty("authorId") val authorId: Long)
