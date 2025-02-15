package pl.jwizard.jwc.core.property.guild

class GuildMultipleProperties(mapSize: Int) : HashMap<GuildProperty, Any>(mapSize) {
	inline fun <reified T> getProperty(key: GuildProperty) = get(key) as T

	inline fun <reified T> getNullableProperty(key: GuildProperty) = get(key) as T?
}
