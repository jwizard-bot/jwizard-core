/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda

/**
 * Represents the statistics details of Discord shards in the bot's environment.
 *
 * @property runningShards The number of shards currently active and running.
 * @property queuedShards The number of shards that are queued and waiting to be initialized.
 * @property avgGatewayPing The average ping (in milliseconds) to the Discord gateway across all shards.
 * @author Miłosz Gilga
 */
data class ShardStatsDetails(
	val runningShards: Int,
	val queuedShards: Int,
	val avgGatewayPing: Double,
)
