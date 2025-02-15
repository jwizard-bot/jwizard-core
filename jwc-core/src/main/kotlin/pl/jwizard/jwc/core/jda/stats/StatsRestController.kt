package pl.jwizard.jwc.core.jda.stats

import io.javalin.http.Context
import pl.jwizard.jwc.core.audio.spi.DistributedAudioClient
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwc.core.jda.stats.dto.ClusterShardStatsInfoResDto
import pl.jwizard.jwc.core.jda.stats.dto.ShardStatsInfo
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.ioc.stereotype.SingletonController
import pl.jwizard.jwl.server.route.RestControllerBase
import pl.jwizard.jwl.server.route.RouteDefinitionBuilder

@SingletonController
internal class StatsRestController(
	private val jdaShardManager: JdaShardManagerBean,
	private val distributedAudioClient: DistributedAudioClient,
	environment: EnvironmentBean,
) : RestControllerBase {
	override val basePath = "/api/v1/stats"

	private val shardStart = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_START)
	private val shardEnd = environment.getProperty<Int>(BotProperty.JDA_SHARDING_OFFSET_END)

	private fun getShardsStatistics(ctx: Context) {
		val shards = jdaShardManager.getShards()
		val shardsStats = shards.map {
			val totalShardListeners = it.guilds.fold(0) { acc, guild ->
				// check only if bot is in audio channel
				if (guild.selfMember.voiceState?.inAudioChannel() == true) {
					val countOfMembers = (guild.selfMember.voiceState?.channel?.members?.size ?: 0)
					acc + (countOfMembers - 1) // remove self user (total - 1)
				} else {
					acc // otherwise return accumulated value
				}
			}
			ShardStatsInfo(
				ping = it.gatewayPing,
				servers = it.guilds.size,
				users = it.users.size - 1, // remove self user (total - 1)
				activeAudioPlayers = distributedAudioClient.getPlayersCountInSelectedGuilds(it.guilds),
				audioListeners = totalShardListeners,
			)
		}
		val resDto = ClusterShardStatsInfoResDto(
			shardOffsetStart = shardStart,
			shardOffsetEnd = shardEnd,
			totalShards = shards.size,
			shards = shardsStats,
		)
		ctx.json(resDto)
	}

	override val routes = RouteDefinitionBuilder()
		.get("/shard", ::getShardsStatistics)
		.compositeRoutes()
}
