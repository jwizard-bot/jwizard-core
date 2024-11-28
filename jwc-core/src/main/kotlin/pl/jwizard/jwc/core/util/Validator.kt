/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

/**
 * Validates whether the provided string is a valid URL with the `http` or `https` protocol.
 *
 * This function uses a regular expression to check if the input string matches the pattern of a valid URL, including
 * the protocol, domain, optional port, and optional path.
 *
 * Example of valid URLs:
 * - http://example.com
 * - https://example.com:8080/path
 *
 * Example of invalid URLs:
 * - invalid_url
 * - ://missing-scheme.com
 *
 * @param url the string to validate as a URL
 * @return `true` if the input string is a valid URL, otherwise `false`
 * @author Miłosz Gilga
 */
fun isValidUrl(url: String): Boolean {
	val urlRegex = "^(https?)://([a-zA-Z0-9.-]+)(:[0-9]+)?(/.*)?$".toRegex()
	return urlRegex.matches(url)
}
