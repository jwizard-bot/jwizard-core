/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import pl.jwizard.core.audio.scheduler.AudioScheduler
import pl.jwizard.core.audio.scheduler.AudioSchedulerContract
import pl.jwizard.core.audio.scheduler.StreamSchedulerFacade
import pl.jwizard.core.audio.scheduler.TrackSchedulerFacade

enum class AudioSourceType(
	private val instantiateFacade: (scheduler: AudioScheduler) -> AudioSchedulerContract
) {
	TRACK({ TrackSchedulerFacade(it) }),
	STREAM({ StreamSchedulerFacade(it) }),
	;

	fun getInstance(scheduler: AudioScheduler) = instantiateFacade(scheduler)
}
