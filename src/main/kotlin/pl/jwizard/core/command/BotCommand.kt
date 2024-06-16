/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

enum class BotCommand(
	val commandName: String,
) {
	// dj
	CLEAR("clear"),
	INFINITE("infinite"),
	JOIN("join"),
	MOVE("move"),
	TRACKSRM("tracksrm"),
	VOLUMECLS("volumecls"),
	SETVOLUME("setvolume"),
	SHUFFLE("shuffle"),
	SKIPTO("skipto"),
	STOP("stop"),

	// manager
	DEBUG("debug"),

	// misc
	HELP("help"),
	HELPME("helpme"),

	// music
	REPEATCLS("repeatcls"),
	PAUSED("paused"),
	PLAYING("playing"),
	GETVOLUME("getvolume"),
	LOOP("loop"),
	PAUSE("pause"),
	PLAY("play"),
	REPEAT("repeat"),
	RESUME("resume"),
	QUEUE("queue"),
	SKIP("skip"),

	// radio
	PLAY_RADIO("radio"),
	STOP_RADIO("radiostop"),
	RADIO_INFO("radioinfo"),
	SHOW_RADIOS("radios"),

	// playlist
	ADDTRACKPL("addtrackpl"),
	ADDQUEUEPL("addqueuepl"),
	ADDPLAYLIST("addplaylist"),
	PLAYPL("playpl"),
	SHOWMEMPL("showmempl"),
	SHOWMYPL("showmypl"),
	SHOWPLSONGS("showplsongs"),

	// vote
	VCLEAR("vclear"),
	VSHUFFLE("vshuffle"),
	VSKIPTO("vskipto"),
	VSKIP("vskip"),
	VSTOP("vstop"),
	;

	fun parseWithPrefix(event: CompoundCommandEvent): String {
		val prefix = if (event.slashCommandEvent == null) event.legacyPrefix else "/"
		return "`${prefix}${commandName}`"
	}
}
