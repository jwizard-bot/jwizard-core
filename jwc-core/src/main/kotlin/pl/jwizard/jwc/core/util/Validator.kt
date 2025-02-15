package pl.jwizard.jwc.core.util

// validates whether the provided string is a valid URL with the `http` or `https` protocol
fun isValidUrl(url: String): Boolean {
	val urlRegex = "^(https?)://([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$".toRegex()
	return urlRegex.matches(url)
}
