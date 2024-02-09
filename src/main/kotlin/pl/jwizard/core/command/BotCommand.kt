/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import pl.jwizard.core.bot.BotConfiguration

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

	// playlist
	ADDTRACKPL("addtrackpl"),
	ADDQUEUEPL("addqueuepl"),
	ADDPLAYLIST("addplaylist"),
	PLAYPL("playpl"),
	SHOWMEMPL("showmempl"),
	SHOWMYPL("showmypl"),
	SHOWPLSONGS("showplsongs"),

	// settings
	SETAUDIOCHN("setaudiochn"),
	SETDJROLE("setdjrole"),
	SETLOCALE("setlocale"),
	SETTRACKREP("settrackrep"),
	SETDEFVOL("setdefvol"),
	SETSKRATIO("setskratio"),
	SETCCHOSSNG("setcchossng"),
	SETRCHOSSNG("setrchossng"),
	SETTCHOSSNG("settchossng"),
	SETTIMEVOT("settimevot"),
	SETTLEAVEM("settleavem"),
	SETTLEAVETR("settleavetr"),

	// vote
	VCLEAR("vclear"),
	VSHUFFLE("vshuffle"),
	VSKIPTO("vskipto"),
	VSKIP("vskip"),
	VSTOP("vstop"),
	;

	fun parseWithPrefix(botConfiguration: BotConfiguration, event: CompoundCommandEvent): String {
		val guildDetails = botConfiguration.guildSettings.getGuildProperties(event.guildId)
		val prefix = if (event.slashCommandEvent == null) guildDetails.legacyPrefix else "/"
		return "`${prefix}${commandName}`"
	}

	companion object {
		fun checkIfCommandExist(name: String): Boolean = entries.any { it.commandName == name }
	}
}
