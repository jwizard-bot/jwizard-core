/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback.rmf

import com.google.gson.JsonElement

data class RmfApiResponse(
	val author: String,
	val title: String,
	val timestamp: Long,
	val coverBigUrl: String,
) {
	constructor(jsonElement: JsonElement) : this(
		jsonElement.asJsonObject["author"].asString,
		jsonElement.asJsonObject["title"].asString,
		jsonElement.asJsonObject["timestamp"].asLong,
		jsonElement.asJsonObject["coverBigUrl"].asString,
	)
}
