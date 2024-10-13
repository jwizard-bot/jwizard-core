/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.refer

import pl.jwizard.jwc.core.integrity.ReferentialIntegrityChecker
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.command.CommandPrefix

/**
 * Enum representing various commands that can be executed in the application. Each command is associated with a string
 * property name used for referencing purposes.
 *
 * @property propName The string representation of the command, which is used for matching and processing JDA commands.
 * @author Miłosz Gilga
 */
enum class Command(override val propName: String) : ReferentialIntegrityChecker, CommandPrefix {
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
	DEBUG("debug"),
	SETTINGS("settings"),
	HELP("help"),
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
	PLAY_RADIO("radio"),
	STOP_RADIO("radiostop"),
	RADIO_INFO("radioinfo"),
	SHOW_RADIOS("radios"),
	ADDTRACKPL("addtrackpl"),
	ADDQUEUEPL("addqueuepl"),
	ADDPLAYLIST("addplaylist"),
	PLAYPL("playpl"),
	SHOWMEMPL("showmempl"),
	SHOWMYPL("showmypl"),
	SHOWPLSONGS("showplsongs"),
	VCLEAR("vclear"),
	VSHUFFLE("vshuffle"),
	VSKIPTO("vskipto"),
	VSKIP("vskip"),
	VSTOP("vstop"),
	;

	/**
	 * Provides the integrity name for the module to ensure that commands conform to expected integrity constraints
	 * within the application.
	 */
	override val moduleIntegrityName = "commands"

	/**
	 * Parses the command by combining the command prefix from the context with the command property name.
	 *
	 * @param context the context containing information about the current command and its execution environment.
	 * @return the full command string with its prefix.
	 */
	override fun parseWithPrefix(context: CommandBaseContext) = "${context.prefix}$propName"
}
