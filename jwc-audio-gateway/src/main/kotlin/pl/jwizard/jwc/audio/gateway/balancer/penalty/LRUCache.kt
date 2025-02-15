package pl.jwizard.jwc.audio.gateway.balancer.penalty

// least recently used (LRU) cache implementation with 0.75 factor
internal class LRUCache<K, V>(private val limit: Int) : LinkedHashMap<K, V>(limit, 0.75f, true) {
	override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?) = size > limit
}
