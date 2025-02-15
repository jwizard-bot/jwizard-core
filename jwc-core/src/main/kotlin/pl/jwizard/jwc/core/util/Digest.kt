package pl.jwizard.jwc.core.util

import java.security.MessageDigest

private val md = MessageDigest.getInstance("MD5")

fun toMD5(input: String): String {
	val digest = md.digest(input.toByteArray())
	return digest.joinToString("") { "%02x".format(it) }
}
