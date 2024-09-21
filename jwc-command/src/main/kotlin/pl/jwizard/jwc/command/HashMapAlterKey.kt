/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

/**
 * An interface that allows objects to provide an alternative key, referred to as [keyAlias].
 *
 * This interface is designed to be implemented by values stored in [TwoWayIntegrityHashMap], enabling the use of both
 * primary keys and alias keys for accessing values in the map.
 *
 * @param K The type of key associated with the alias.
 * @author Miłosz Gilga
 * @see TwoWayIntegrityHashMap
 */
interface HashMapAlterKey<K> {

	/**
	 * Provides an alternative key (alias) that can be used in place of the primary key. Classes implementing this
	 * interface must define how the alias key is provided.
	 *
	 * @return The alternative key (alias) associated with the object.
	 */
	val keyAlias: K
}
