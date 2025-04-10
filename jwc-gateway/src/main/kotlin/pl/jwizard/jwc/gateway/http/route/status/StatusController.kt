package pl.jwizard.jwc.gateway.http.route.status

import net.dv8tion.jda.api.JDA
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.audio.DistributedAudioClient
import pl.jwizard.jwc.core.jda.JdaShardManager
import pl.jwizard.jwc.gateway.http.BasicAuthRouteHandler
import pl.jwizard.jwc.gateway.http.route.status.dto.ReducedShardStatusResDto
import pl.jwizard.jwc.gateway.http.route.status.dto.ShardStatusInfoResDto
import pl.jwizard.jwc.gateway.http.route.status.dto.ShardUpInGuildCheckResDto
import pl.jwizard.jwc.gateway.http.route.status.dto.ShardsCountResDto
import pl.jwizard.jwl.server.route.HttpControllerBase
import pl.jwizard.jwl.server.route.RouteDefinitionBuilder
import kotlin.math.ceil

@Component
internal class StatusController(
	private val jdaShardManager: JdaShardManager,
	private val distributedAudioClient: DistributedAudioClient,
) : HttpControllerBase {
	override val basePath = "/api/v1/status"

	private val getReducedShardsStatistics = BasicAuthRouteHandler { ctx ->
		val shards = jdaShardManager.runningShards
		val separatedShardsInfo = getParsedShardStats(shards)

		val reducedShardsInfo = separatedShardsInfo.reduce { acc, shard ->
			ShardStatusInfoResDto(
				0, // ignored, can be 0 or something else
				acc.ping + shard.ping,
				acc.servers + shard.servers,
				acc.users + shard.users,
				acc.activeAudioPlayers + shard.activeAudioPlayers,
				acc.audioListeners + shard.audioListeners
			)
		}
		val resDto = ReducedShardStatusResDto(
			totalShards = shards.size,
			avgShardGatewayPing = ceil(reducedShardsInfo.ping.toDouble() / shards.size).toInt(),
			servers = reducedShardsInfo.servers,
			users = reducedShardsInfo.users,
			activeAudioPlayers = reducedShardsInfo.activeAudioPlayers,
			audioListeners = reducedShardsInfo.audioListeners,
		)
		ctx.json(resDto)
	}

	private val getShardsStatistics = BasicAuthRouteHandler { ctx ->
		val resDto = getParsedShardStats(jdaShardManager.runningShards)
		ctx.json(resDto)
	}

	private val getShardsCount = BasicAuthRouteHandler { ctx ->
		val resDto = ShardsCountResDto(up = jdaShardManager.runningShards.size)
		ctx.json(resDto)
	}

	private val checkIfShardIsUpInGuild = BasicAuthRouteHandler { ctx ->
		val guildNameOrId = ctx.queryParam("guild")
		var result = false
		if (guildNameOrId != null) {
			result = jdaShardManager.runningShards.any {
				it.guilds.any { guild ->
					guild.id == guildNameOrId || guild.name.lowercase().contains(guildNameOrId.lowercase())
				}
			}
		}
		val resDto = ShardUpInGuildCheckResDto(result)
		ctx.json(resDto)
	}

	private fun getParsedShardStats(shards: List<JDA>) = shards
		.map {
			val totalShardListeners = it.guilds.fold(0) { acc, guild ->
				// check only if bot is in audio channel
				if (guild.selfMember.voiceState?.inAudioChannel() == true) {
					val countOfMembers = (guild.selfMember.voiceState?.channel?.members?.size ?: 0)
					acc + (countOfMembers - 1) // remove self user (total - 1)
				} else {
					acc // otherwise return accumulated value
				}
			}
			ShardStatusInfoResDto(
				id = it.shardInfo.shardId,
				ping = it.gatewayPing,
				servers = it.guilds.size,
				users = it.users.size - 1, // remove self user (total - 1)
				activeAudioPlayers = distributedAudioClient.getPlayersCountInSelectedGuilds(it.guilds),
				audioListeners = totalShardListeners,
			)
		}
		.sortedBy(ShardStatusInfoResDto::id)

	override val routes = RouteDefinitionBuilder()
		.get("/shard/all", getShardsStatistics)
		.get("/shard/reduced", getReducedShardsStatistics)
		.get("/shard/count", getShardsCount)
		.get("/shard/check", checkIfShardIsUpInGuild)
		.compositeRoutes()
}
