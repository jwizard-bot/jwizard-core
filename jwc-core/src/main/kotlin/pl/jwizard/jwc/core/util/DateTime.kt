package pl.jwizard.jwc.core.util

import java.time.Duration
import java.util.concurrent.TimeUnit

// millis to HH:mm:ss
fun millisToDTF(millis: Long) = dtFormat(Duration.ofMillis(millis))

// seconds to HH:mm:ss
fun secToDTF(sec: Long) = dtFormat(Duration.ofSeconds(sec))

// if there are no minutes, it returns seconds (ex. "30s")
// if there are no remaining seconds, it returns minutes (ex. "5m")
// if there are both minutes and seconds, it returns them formatted as "Xm, Ys" (ex. "5m, 30s")
fun floatingSecToMin(seconds: Long): String {
	val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
	val remainingSeconds = seconds % 60
	return when {
		minutes == 0L -> "${remainingSeconds}s"
		remainingSeconds == 0L -> "${minutes}m"
		else -> "%dm, %02ds".format(minutes, remainingSeconds)
	}
}

fun fromTimeToNow(
	timestamp: Long,
): Duration = Duration.ofSeconds(timestamp - System.currentTimeMillis() / 1000)

fun fromNowToTime(
	timestamp: Long,
): Duration = Duration.ofSeconds(System.currentTimeMillis() / 1000 - timestamp)

fun dtFormat(duration: Duration): String = "%02d:%02d:%02d".format(
	duration.toHoursPart(),
	duration.toMinutesPart(),
	duration.toSecondsPart()
)
