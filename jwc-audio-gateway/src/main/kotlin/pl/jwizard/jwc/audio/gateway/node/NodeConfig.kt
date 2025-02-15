package pl.jwizard.jwc.audio.gateway.node

import pl.jwizard.jwc.audio.gateway.balancer.region.RegionGroup

class NodeConfig private constructor(
	private val hostName: String,
	private val port: Int,
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
	) = "$protocol${if (secure) "s" else ""}://${hostName}:${port}"

	class Builder {
		private var hostName: String? = null
		private var port: Int? = null
		private var secure: Boolean? = null
		private var name: String? = null
		private var password: String? = null
		private var pool: NodePool? = null
		private var regionGroup: RegionGroup = RegionGroup.UNKNOWN
		private var httpTimeout: Long? = null

		fun setAddress(hostName: String, port: Int, secure: Boolean) = apply {
			this.hostName = hostName
			this.port = port
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
			requireNotNull(hostName) { "Hostname must be set" }
			requireNotNull(port) { "Port must be set" }
			requireNotNull(name) { "Name must be set" }
			requireNotNull(password) { "Password must be set" }
			requireNotNull(pool) { "Pool must be set" }
			requireNotNull(httpTimeout) { "Http timeout must be set" }
			return NodeConfig(
				hostName!!,
				port!!,
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
