package pl.jwizard.jwc.core.server.route.status.dto

internal data class ReducedShardStatusResDto(
	val totalShards: Int,
	val avgShardGatewayPing: Int,
	val servers: Int,
	val users: Int,
	val activeAudioPlayers: Int,
	val audioListeners: Int,
)
