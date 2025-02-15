package pl.jwizard.jwc.core.jda.stats.dto

internal data class ShardStatsInfo(
	val ping: Long,
	val servers: Int,
	val users: Int,
	val activeAudioPlayers: Int,
	val audioListeners: Int,
)
