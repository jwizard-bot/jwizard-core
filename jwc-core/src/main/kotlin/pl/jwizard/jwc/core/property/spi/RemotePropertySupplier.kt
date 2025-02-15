package pl.jwizard.jwc.core.property.spi

import kotlin.reflect.KClass

interface RemotePropertySupplier {
	fun <T : Any> getProperty(columnName: String, guildId: Long, type: KClass<T>): T?

	fun getCombinedProperties(columnNames: List<String>, guildId: Long): Map<String, Any?>
}
