/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.utils

import java.time.Duration

object DateUtils {
	fun convertMilisToDate(milis: Long): String {
		val duration = Duration.ofMillis(milis)
		return "%02d:%02d:%02d".format(duration.toHours(), duration.toMillisPart(), duration.toSecondsPart())
	}
}
