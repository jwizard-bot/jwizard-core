/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.audio

import pl.jwizard.core.audio.scheduler.SchedulerActions

data class TrackPosition(
	val previous: Int,
	val selected: Int
) {
	fun checkBounds(actions: SchedulerActions): Boolean = actions.checkInvertedTrackPosition(previous)
		|| actions.checkInvertedTrackPosition(selected)

	fun isSamePosition(): Boolean = previous == selected
}
