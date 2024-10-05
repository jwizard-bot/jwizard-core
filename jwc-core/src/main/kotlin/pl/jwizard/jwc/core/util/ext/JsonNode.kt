/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util.ext

import com.fasterxml.jackson.databind.JsonNode

/**
 * Retrieves the text value associated with the specified key from a [JsonNode].
 *
 * This function attempts to retrieve the value of the specified key and returns it as a String. If the key does not
 * exist or the value is null, it returns null.
 *
 * @param key The key whose associated value is to be retrieved.
 * @return The text value associated with the specified key, or null if the key does not exist or has no value.
 * @author Miłosz Gilga
 */
fun JsonNode.getAsText(key: String): String? = this.get(key).asText()

/**
 * Retrieves the long value associated with the specified key from a [JsonNode].
 *
 * This function retrieves the value of the specified key and converts it to a Long. If the key does not exist, it
 * returns 0.
 *
 * @param key The key whose associated value is to be retrieved.
 * @return The long value associated with the specified key, or 0 if the key does not exist.
 * @author Miłosz Gilga
 */
fun JsonNode.getAsLong(key: String) = this.get(key).asLong()
