package pl.jwizard.jwc.audio.gateway.node

import pl.jwizard.jwc.audio.gateway.balancer.region.RegionGroup

class NodeConfig private constructor(
	private val hostWithPort: String,
	private val secure: Boolean,
	val name: String,
	val password: String,
	val pool: NodePool,
	val regionGroup: RegionGroup = RegionGroup.UNKNOWN,
	val httpTimeout: Long,
) {
	val wsUrl = getUrlWithProtocol("ws")
	val httpUrl = getUrlWithProtocol("http")

	private fun getUrlWithProtocol(
		protocol: String,
	) = "$protocol${if (secure) "s" else ""}://${hostWithPort}"

	class Builder {
		private var hostWithPort: String? = null
		private var secure: Boolean? = null
		private var name: String? = null
		private var password: String? = null
		private var pool: NodePool? = null
		private var regionGroup: RegionGroup = RegionGroup.UNKNOWN
		private var httpTimeout: Long? = null

		fun setAddress(hostWithPort: String, secure: Boolean) = apply {
			this.hostWithPort = hostWithPort
			this.secure = secure
		}

		fun setHostDescriptor(name: String, password: String) = apply {
			this.name = name
			this.password = password
		}

		fun setHttpTimeout(httpTimeout: Long) = apply {
			this.httpTimeout = httpTimeout
		}

		fun setBalancerSetup(pool: NodePool, regionGroup: String) = apply {
			this.pool = pool
			this.regionGroup = RegionGroup.fromRawValue(regionGroup)
		}

		fun build(): NodeConfig {
			requireNotNull(hostWithPort) { "Host with port must be set" }
			requireNotNull(name) { "Name must be set" }
			requireNotNull(password) { "Password must be set" }
			requireNotNull(pool) { "Pool must be set" }
			requireNotNull(httpTimeout) { "Http timeout must be set" }
			return NodeConfig(
				hostWithPort!!,
				secure!!,
				name!!,
				password!!,
				pool!!,
				regionGroup,
				httpTimeout!!,
			)
		}
	}

	override fun toString() = name
}
