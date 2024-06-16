/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

import java.time.Duration
import java.util.concurrent.TimeUnit

object DateUtils {
	private val SEC = TimeUnit.SECONDS
	private val MIN = TimeUnit.MINUTES

	fun convertMilisToDTF(milis: Long): String = dtfFormat(Duration.ofMillis(milis))

	fun convertSecToDTF(seconds: Long): String = dtfFormat(Duration.ofSeconds(seconds))

	private fun dtfFormat(duration: Duration): String = "%02d:%02d:%02d".format(
		duration.toHoursPart(),
		duration.toMinutesPart(),
		duration.toSecondsPart()
	)

	fun convertSecToMin(seconds: Long): String {
		val minutes = SEC.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(SEC.toHours(seconds))
		if (minutes == 0L) {
			return "${SEC.toSeconds(seconds) - MIN.toSeconds(SEC.toMinutes(seconds))}s"
		}
		if ((seconds % 60) == 0L) {
			return "${minutes}m" // if has not seconds return only minutes
		}
		return "%dm, %02ds".format(
			minutes,
			SEC.toSeconds(seconds) - MIN.toSeconds(SEC.toMinutes(seconds))
		)
	}

	fun convertSecToMin(seconds: Int): String = convertSecToMin(seconds.toLong())

	fun getTimeToDuration(timestamp: Long): String =
		dtfFormat(Duration.ofSeconds(timestamp - System.currentTimeMillis() / 1000))
}
