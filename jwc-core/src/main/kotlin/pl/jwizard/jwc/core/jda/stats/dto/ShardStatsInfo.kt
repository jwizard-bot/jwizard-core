/*
 * Copyright (c) 2025 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.stats.dto

data class ShardStatsInfo(
	val ping: Long,
	val servers: Int,
	val users: Int,
	val activeAudioPlayers: Int,
	val audioListeners: Int,
)
