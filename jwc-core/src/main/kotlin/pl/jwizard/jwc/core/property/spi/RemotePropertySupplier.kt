/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.spi

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
	fun <T : Any> getProperty(columnName: String, guildId: Long, type: KClass<T>): T?

	/**
	 * Retrieves multiple properties for a given guild from the remote source.
	 *
	 * This method allows fetching several properties at once based on a list of column names. It is useful for retrieving
	 * multiple configuration settings for a specific guild in a single call. The result is returned as a map where each
	 * key is a column name and each value is the corresponding property value.
	 *
	 * @param columnNames A list of column names to retrieve values for.
	 * @param guildId The ID of the guild for which to retrieve the properties.
	 * @return A map where each key is a column name, and the value is the property value for that guild.
	 */
	fun getCombinedProperties(columnNames: List<String>, guildId: Long): Map<String, Any?>
}
