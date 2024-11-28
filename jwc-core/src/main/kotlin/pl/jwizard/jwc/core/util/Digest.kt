/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import java.security.MessageDigest

/**
 * Instance of the MD5 [MessageDigest] to perform MD5 hashing.
 *
 * @author Miłosz Gilga
 */
private val md = MessageDigest.getInstance("MD5")

/**
 * Converts the given input string to an MD5 hash.
 *
 * This function takes a string as input, applies the MD5 hashing algorithm,
 * and returns the resulting hash as a hexadecimal string. The MD5 algorithm
 * generates a 128-bit hash, represented as a 32-character hexadecimal number.
 *
 * Example usage:
 * ```
 * toMD5("Hello, World!") // returns "65a8e27d8879283831b664bd8b7f0ad4"
 * ```
 *
 * @param input The string to be converted to MD5 hash.
 * @return A string representing the MD5 hash of the input.
 * @author Miłosz Gilga
 */
fun toMD5(input: String): String {
	val digest = md.digest(input.toByteArray())
	return digest.joinToString("") { "%02x".format(it) }
}
