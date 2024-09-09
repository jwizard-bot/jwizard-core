/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.stereotype

import kotlin.reflect.KClass

/**
 * Interface for supplying remote properties.
 *
 * This interface provides methods to fetch global properties and per-guild properties based on certain parameters.
 * Properties are fetched from database, in `jwc-persistence` module.
 *
 * @author Miłosz Gilga
 */
interface RemotePropertySupplier {

	/**
	 * Retrieves all global properties as a map of key-value pairs.
	 *
	 * The global properties are typically configuration settings that apply across all guilds or contexts. The returned
	 * map contains key-value pairs where the key is the property name and the value is a pair consisting of the property
	 * value and its type.
	 *
	 * @return A mutable map of global properties, where the key is the property name and the value is a pair of property
	 *         value and its type.
	 */
	fun getGlobalProperties(): MutableMap<String, Pair<String, String>>

	/**
	 * Retrieves a specific property for a given guild from the remote source.
	 *
	 * This method fetches a property based on the column name, guild ID, and type. It is used to get properties that are
	 * specific to a particular guild. If the property does not exist, it returns null.
	 *
	 * @param T The type of the property value.
	 * @param columnName The name of the column in the database from which to retrieve the property.
	 * @param guildId The ID of the guild for which to retrieve the property.
	 * @param type The class of the property type.
	 * @return The property value of type [T], or null if the property is not found.
	 */
	fun <T : Any> getProperty(columnName: String, guildId: String, type: KClass<T>): T?
}
