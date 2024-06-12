/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

import org.apache.commons.lang3.StringUtils

enum class GuildDbProperty(
	val columnName: String,
	val defaultValue: Any?,
) {
	VOTING_PERCENTAGE_RATIO("voting_percentage_ratio", 50),
	MAX_VOTING_TIME("time_to_finish_voting_sec", 120),
	MUSIC_TEXT_CHANNEL_ID("music_text_channel_id", StringUtils.EMPTY),
	DJ_ROLE_NAME("dj_role_name", "DJWizard"),
	MAX_REPEATS_OF_TRACK("max_repeats_of_track", 30),
	LEAVE_EMPTY_CHANNEL_SEC("leave_empty_channel_sec", 120),
	LEAVE_NO_TRACKS_SEC("leave_no_tracks_channel_sec", 120),
	DEFAULT_VOLUME("default_volume", 100),
	RANDOM_AUTO_CHOOSE_TRACK("random_auto_choose_track", true),
	TIME_AFTER_AUTO_CHOOSE_SEC("time_after_auto_choose_sec", 30),
	MAX_TRACKS_TO_CHOOSE("tracks_to_choose_max", 10),
	;
}