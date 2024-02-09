/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.settings

data class GuildSettingsDetails(
	val legacyPrefix: String,
	val djRoleName: String,
	val locale: String,
	val slashEnabled: Boolean,
	val enabledCommands: List<String>,
	val enabledSlashCommands: List<String>,
	val inactivity: Inactivity,
	val voting: Voting,
	val audioPlayer: AudioPlayer,
	val enabledModules: List<String>,
	val musicTextChannelId: String?,
)

data class Inactivity(
	val leaveEmptyChannelSec: Long,
	val leaveNoTracksChannelSec: Long,
)

data class Voting(
	val timeToFinishSec: Long,
	val percentageRatio: Short,
)

data class AudioPlayer(
	val timeAfterAutoChooseSec: Long,
	val tracksNumberChoose: Long,
	val randomAutoChoose: Boolean,
	val maxRepeatsOfTrack: Long,
	val defaultVolume: Long,
)
