/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

data class VotingSongChooserSettings(
	val randomAutoChooseTrack: Boolean,
	val timeAfterAutoChooseSec: Int,
	val trackToChooseMax: Int,
) {
	constructor() : this(
		randomAutoChooseTrack = GuildDbProperty.RANDOM_AUTO_CHOOSE_TRACK.defaultValue as Boolean,
		timeAfterAutoChooseSec = GuildDbProperty.TIME_AFTER_AUTO_CHOOSE_SEC.defaultValue as Int,
		trackToChooseMax = GuildDbProperty.MAX_TRACKS_TO_CHOOSE.defaultValue as Int,
	)
}
