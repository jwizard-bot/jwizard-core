/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.jdbc

import org.apache.commons.lang3.StringUtils

object JdbcUtils {
	fun parse(input: String?, replacements: Map<String, Any>): String {
		var result = input
		for ((key, value) in replacements) {
			result = StringUtils.replace(result, "{{$key}}", value.toString())
		}
		return result ?: StringUtils.EMPTY
	}
}