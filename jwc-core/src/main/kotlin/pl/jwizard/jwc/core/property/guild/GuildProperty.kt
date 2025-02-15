package pl.jwizard.jwc.core.property.guild

import pl.jwizard.jwc.core.property.guild.GuildPropertyConverter.*
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.property.Property
import java.math.BigInteger
import kotlin.reflect.KClass

enum class GuildProperty(
	override val key: String,
	override val placeholder: String = "",
	val converter: GuildPropertyConverter? = null,
	val nonDefaultType: KClass<*>? = null,
) : Property, I18nLocaleSource {
	// represents a unique identifier for a database guild entry
	DB_ID("id", "", null, BigInteger::class),

	// represents the language tag associated with guild
	LANGUAGE_TAG("language", "jwc.guild.prop.languageTag", BASE, String::class),

	// ratio of voting percentage for guilds
	VOTING_PERCENTAGE_RATIO(
		"voting_percentage_ratio",
		"jwc.guild.prop.votingPercentageRatio",
		TO_PERCENTAGE
	),

	// maximum voting time for guilds in seconds
	MAX_VOTING_TIME_SEC("time_to_finish_voting_sec", "jwc.guild.prop.voteMaxWaitingTime", TO_DTF_SEC),

	// id of the music text channel in the guild. Stored as a number
	MUSIC_TEXT_CHANNEL_ID(
		"music_text_channel_id",
		"jwc.guild.prop.musicTextChannel",
		BASE,
		Long::class
	),

	// name of the DJ role in the guild
	DJ_ROLE_NAME("dj_role_name", "jwc.guild.prop.djRole", BASE),

	/**
	 * minimum number of repeats allowed for a track in the guild
	 */
	MIN_REPEATS_OF_TRACK("min_repeats_of_track", "jwc.guild.prop.minRepeatsOfTrack", BASE),

	/**
	 * maximum number of repeats allowed for a track in the guild
	 */
	MAX_REPEATS_OF_TRACK("max_repeats_of_track", "jwc.guild.prop.maxRepeatsOfTrack", BASE),

	// time in seconds after which the bot leaves an empty channel in the guild
	LEAVE_EMPTY_CHANNEL_SEC(
		"leave_empty_channel_sec",
		"jwc.guild.prop.leaveEmptyChannelSec",
		TO_DTF_SEC
	),

	// time in seconds after which the bot leaves a channel with no tracks in the guild
	LEAVE_NO_TRACKS_SEC("leave_no_tracks_channel_sec", "jwc.guild.prop.leaveNoTracksSec", TO_DTF_SEC),

	// default volume level for the guild
	PLAYER_VOLUME("player_volume", "jwc.guild.prop.playerVolume", TO_PERCENTAGE),

	// indicates whether to randomly auto-choose tracks in the guild
	RANDOM_AUTO_CHOOSE_TRACK(
		"random_auto_choose_track",
		"jwc.guild.prop.randomAutoChooseTrack",
		TO_BOOL
	),

	// time in seconds after which the bot automatically chooses a track in the guild
	TIME_AFTER_AUTO_CHOOSE_SEC(
		"time_after_auto_choose_sec",
		"jwc.guild.prop.timeAfterAutoChooseTrack",
		TO_DTF_SEC
	),

	// maximum number of tracks to choose from in the guild
	MAX_TRACKS_TO_CHOOSE("tracks_to_choose_max", "jwc.guild.prop.maxTracksToChoose", BASE),

	// the legacy command prefix used by the bot in guilds
	LEGACY_PREFIX("legacy_prefix", "jwc.guild.prop.legacyPrefix", BASE),

	// indicates whether slash commands are enabled for the guild
	SLASH_ENABLED("slash_enabled", "jwc.guild.prop.slashEnabled", TO_BOOL),

	// determines if notifications from bot responses should be suppressed
	SUPPRESS_RESPONSE_NOTIFICATIONS(
		"suppress_response_notifications",
		"jwc.guild.prop.suppressNotifications",
		TO_BOOL
	),
	;
}
