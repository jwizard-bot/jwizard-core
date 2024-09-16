/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.loader

import pl.jwizard.jwc.core.property.PropertySourceData
import pl.jwizard.jwc.core.property.spi.RemotePropertySupplier
import pl.jwizard.jwc.core.util.KtCast

/**
 * Load properties from an external data source (database) using the [RemotePropertySupplier]. These properties are
 * then cast to the appropriate types using the [KtCast] utility.
 *
 * @property remotePropertySupplier Provides access to remote properties, fetched from a database.
 * @author Miłosz Gilga
 */
class DbPropertySourceLoader(
	private val remotePropertySupplier: RemotePropertySupplier
) : PropertySourceData(DbPropertySourceLoader::class) {

	/**
	 * Loads and sets properties from the remote data source by fetching global properties and converting them to
	 * the appropriate types.
	 *
	 * @return A map of property keys and their corresponding typed values.
	 */
	override fun setProperties(): Map<Any, Any> {
		val rawProperties = remotePropertySupplier.getGlobalProperties()
		return rawProperties
			.map {
				val (value, type) = it.value
				it.key to KtCast.castToValue(value, type)
			}
			.toMap()
	}
}
