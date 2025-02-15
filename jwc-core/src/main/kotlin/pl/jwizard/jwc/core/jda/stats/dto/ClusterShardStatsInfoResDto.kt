package pl.jwizard.jwc.core.jda.stats.dto

data class ClusterShardStatsInfoResDto(
	val shardOffsetStart: Int,
	val shardOffsetEnd: Int,
	val totalShards: Int,
	val shards: List<ShardStatsInfo>,
)
