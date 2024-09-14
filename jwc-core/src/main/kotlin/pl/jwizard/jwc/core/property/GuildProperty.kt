/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property

import kotlin.reflect.KClass

/**
 * Enum class representing configuration properties specific to guilds.
 *
 * Defining following properties:
 *
 * - [VOTING_PERCENTAGE_RATIO]: Ratio of voting percentage for guilds.
 * - [MAX_VOTING_TIME]: Maximum voting time for guilds in seconds.
 * - [MUSIC_TEXT_CHANNEL_ID]: ID of the music text channel in the guild. Stored as a string.
 * - [DJ_ROLE_NAME]: Name of the DJ role in the guild.
 * - [MAX_REPEATS_OF_TRACK]: Maximum number of repeats allowed for a track in the guild.
 * - [LEAVE_EMPTY_CHANNEL_SEC]: Time in seconds after which the bot leaves an empty channel in the guild.
 * - [LEAVE_NO_TRACKS_SEC]: Time in seconds after which the bot leaves a channel with no tracks in the guild.
 * - [DEFAULT_VOLUME]: Default volume level for the guild.
 * - [RANDOM_AUTO_CHOOSE_TRACK]: Indicates whether to randomly auto-choose tracks in the guild.
 * - [TIME_AFTER_AUTO_CHOOSE_SEC]: Time in seconds after which the bot automatically chooses a track in the guild.
 * - [MAX_TRACKS_TO_CHOOSE]: Maximum number of tracks to choose from in the guild.
 *
 * @property key The name of the database column that corresponds to this property.
 * @property nonDefaultType The type of the property value. By default, guild property refer to [BotProperty] as
 * 					 default value. If default value not exist, this field must not be null.
 * @author Miłosz Gilga
 * @see BotProperty
 */
enum class GuildProperty(
	override val key: String,
	val nonDefaultType: KClass<*>? = null,
): Property {

	/**
	 * Ratio of voting percentage for guilds.
	 */
	VOTING_PERCENTAGE_RATIO("voting_percentage_ratio"),

	/**
	 * Maximum voting time for guilds in seconds.
	 */
	MAX_VOTING_TIME("time_to_finish_voting_sec"),

	/**
	 * ID of the music text channel in the guild. Stored as a string.
	 */
	MUSIC_TEXT_CHANNEL_ID("music_text_channel_id", String::class),

	/**
	 * Name of the DJ role in the guild.
	 */
	DJ_ROLE_NAME("dj_role_name"),

	/**
	 * Maximum number of repeats allowed for a track in the guild.
	 */
	MAX_REPEATS_OF_TRACK("max_repeats_of_track"),

	/**
	 * Time in seconds after which the bot leaves an empty channel in the guild.
	 */
	LEAVE_EMPTY_CHANNEL_SEC("leave_empty_channel_sec"),

	/**
	 * Time in seconds after which the bot leaves a channel with no tracks in the guild.
	 */
	LEAVE_NO_TRACKS_SEC("leave_no_tracks_channel_sec"),

	/**
	 * Default volume level for the guild.
	 */
	DEFAULT_VOLUME("default_volume"),

	/**
	 * Indicates whether to randomly auto-choose tracks in the guild.
	 */
	RANDOM_AUTO_CHOOSE_TRACK("random_auto_choose_track"),

	/**
	 * Time in seconds after which the bot automatically chooses a track in the guild.
	 */
	TIME_AFTER_AUTO_CHOOSE_SEC("time_after_auto_choose_sec"),

	/**
	 * Maximum number of tracks to choose from in the guild.
	 */
	MAX_TRACKS_TO_CHOOSE("tracks_to_choose_max"),
	;
}
