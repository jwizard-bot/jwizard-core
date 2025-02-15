package pl.jwizard.jwc.audio.gateway.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.arbjerg.lavalink.protocol.v4.json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

val objectMapper = ObjectMapper()

inline fun <reified T> fromJsonElement(jsonElement: JsonElement): T {
	val stringValue = jsonElement.toString()
	return objectMapper.readValue<T>(stringValue)
}

fun toJsonObject(data: Any?): JsonObject {
	val jsonNode = objectMapper.readTree(data.toString())
	return toJsonElement(jsonNode) as JsonObject
}

fun toJsonElement(data: Any?) = if (data == null) {
	JsonObject(mapOf())
} else {
	val jsonString = objectMapper.writeValueAsString(data)
	json.parseToJsonElement(jsonString)
}
