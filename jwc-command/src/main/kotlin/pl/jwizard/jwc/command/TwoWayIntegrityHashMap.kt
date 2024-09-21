/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import java.util.concurrent.ConcurrentHashMap

/**
 * A custom implementation of a bidirectional integrity map.
 *
 * This class extends the [ConcurrentHashMap] but allows querying of values based on both the original key and an alias
 * key that might be embedded in the values.
 *
 * @param K The type of keys maintained by this map.
 * @param V The type of mapped values which must implement the [HashMapAlterKey] interface.
 * @author Miłosz Gilga
 * @see HashMapAlterKey
 * @see ConcurrentHashMap
 */
class TwoWayIntegrityHashMap<K, V : HashMapAlterKey<K>> : ConcurrentHashMap<K, V>() {

	/**
	 * Retrieves the value associated with the specified key. If no direct match is found, it searches through the values
	 * and returns the first one where the `keyAlias` matches the provided key. This allows for an alternative key
	 * mechanism in the map.
	 *
	 * @param key The key whose associated value is to be returned, or an alias to be checked.
	 * @return The value to which the specified key is mapped, or `null` if this map contains no mapping for the key or
	 * 				 its alias.
	 */
	override operator fun get(key: K) = super.get(key) ?: values.find { it.keyAlias == key }
}
