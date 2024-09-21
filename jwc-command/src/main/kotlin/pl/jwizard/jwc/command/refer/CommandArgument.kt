/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.refer

/**
 * Enum representing the various arguments that can be associated with commands in the application. Each argument is
 * linked to a specific property name used for reference in command execution.
 *
 * @property propName The string representation of the argument, which is used for matching and processing command
 * 					 arguments.
 * @author Miłosz Gilga
 */
enum class CommandArgument(override val propName: String) : ReferentialIntegrityChecker {
	TRACK("track"),
	COUNT("count"),
	VOLUME("volume"),
	MEMBER("member"),
	POS("pos"),
	FROM_POS("fromPos"),
	TO_POS("toPos"),
	PLAYLIST_NAME_OR_ID("playlistNameOrId"),
	PLAYLIST_NAME("playlistName"),
	RADIO_STATION("radioStation"),
	;

	/**
	 * Provides the integrity name for the module to ensure that command arguments conform to expected integrity
	 * constraints within the application.
	 */
	override val moduleIntegrityName
		get() = "command arguments"
}
