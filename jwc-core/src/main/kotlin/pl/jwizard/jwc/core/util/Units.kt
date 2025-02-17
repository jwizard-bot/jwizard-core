package pl.jwizard.jwc.core.util

private const val ONE_KB = 1024

private val units = arrayOf("B", "KB", "MB", "GB", "TB", "PB", "EB")

// formats a given number of bytes into a human-readable format
// - formatBytes(null) returns "0 B"
// - formatBytes(1500) returns "1.46 KB"
// - formatBytes(1048576) returns "1.00 MB"
fun formatBytes(bytes: Long?) = when {
	bytes == null -> "0 B"
	bytes < ONE_KB -> "$bytes B"
	else -> {
		var size = bytes.toDouble()
		var unitIndex = 0
		while (size >= ONE_KB && unitIndex < units.size - 1) {
			size /= ONE_KB
			unitIndex++
		}
		"%.2f %s".format(size, units[unitIndex])
	}
}
