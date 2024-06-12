/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

data class GuildCombinedPropertiesDto(
	val votingPercentageRatio: Int,
	val timeToFinishVotingSec: Int,
	val musicTextChannelId: String?,
	val maxRepeatsOfTrack: Int,
	val leaveEmptyChannelSec: Int,
	val leaveNoTracksChannelSec: Int,
	val defaultVolume: Int,
	val randomAutoChooseTrack: Boolean,
	val timeAfterAutoChooseSec: Int,
	val tracksToChooseMax: Int
)
