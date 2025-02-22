package pl.jwizard.jwc.core.server.route.status.dto

internal data class ShardStatusInfoResDto(
	val id: Int,
	val ping: Long,
	val servers: Int,
	val users: Int,
	val activeAudioPlayers: Int,
	val audioListeners: Int,
)
