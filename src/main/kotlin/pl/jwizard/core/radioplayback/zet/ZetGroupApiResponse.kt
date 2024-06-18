/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.radioplayback.zet

import com.google.gson.JsonElement

data class ZetGroupApiResponse(
	val title: String,
	val artist: String,
	val duration: String,
	val img: String,
) {
	constructor(jsonElement: JsonElement) : this(
		jsonElement.asJsonObject["title"].asString,
		jsonElement.asJsonObject["artist"].asString,
		jsonElement.asJsonObject["duration"].asString,
		jsonElement.asJsonObject["img"].asString,
	)
}
