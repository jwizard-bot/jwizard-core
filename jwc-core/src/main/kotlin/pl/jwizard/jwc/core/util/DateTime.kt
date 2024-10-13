/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Converts a duration in milliseconds to a formatted string in the format HH:mm:ss.
 *
 * @param millis The duration in milliseconds.
 * @return A formatted string representing the duration.
 * @author Miłosz Gilga
 */
fun millisToDTF(millis: Long) = dtFormat(Duration.ofMillis(millis))

/**
 * Converts a duration in seconds to a formatted string in the format HH:mm:ss.
 *
 * @param sec The duration in seconds.
 * @return A formatted string representing the duration.
 * @author Miłosz Gilga
 */
fun secToDTF(sec: Long) = dtFormat(Duration.ofSeconds(sec))

/**
 * Converts a duration in seconds to a human-readable string format.
 *
 * This function returns a string representing the duration in minutes and seconds. The output varies:
 * - If there are no minutes, it returns seconds (ex. "30s").
 * - If there are no remaining seconds, it returns minutes (ex. "5m").
 * - If there are both minutes and seconds, it returns them formatted as "Xm, Ys" (ex. "5m, 30s").
 *
 * @param seconds The duration in seconds.
 * @return A human-readable string representing the duration.
 * @author Miłosz Gilga
 */
fun floatingSecToMin(seconds: Long): String {
	val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
	val remainingSeconds = seconds % 60
	return when {
		minutes == 0L -> "${remainingSeconds}s"
		remainingSeconds == 0L -> "${minutes}m"
		else -> "%dm, %02ds".format(minutes, remainingSeconds)
	}
}

/**
 * Calculates the duration from a given timestamp to the current time.
 *
 * @param timestamp The timestamp in seconds from which to calculate the duration.
 * @return The duration from the given timestamp to now.
 * @author Miłosz Gilga
 */
fun fromTimeToNow(timestamp: Long): Duration = Duration.ofSeconds(timestamp - System.currentTimeMillis() / 1000)

/**
 * Calculates the duration from the current time to a given timestamp.
 *
 * @param timestamp The timestamp in seconds to which to calculate the duration.
 * @return The duration from now to the given timestamp.
 * @author Miłosz Gilga
 */
fun fromNowToTime(timestamp: Long): Duration = Duration.ofSeconds(System.currentTimeMillis() / 1000 - timestamp)

/**
 * Formats a given Duration into a string in the format HH:mm:ss.
 *
 * @param duration The Duration to format.
 * @return A formatted string representing the duration.
 * @author Miłosz Gilga
 */
fun dtFormat(duration: Duration): String = "%02d:%02d:%02d".format(
	duration.toHoursPart(),
	duration.toMinutesPart(),
	duration.toSecondsPart()
)
