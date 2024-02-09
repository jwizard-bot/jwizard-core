/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.arg

enum class CommandArgument(
	val argName: String,
) {
	TEXT_CHANNEL("textChannel"),
	ROLE_NAME("roleName"),
	LOCALE_CODE("localeCode"),
	REPEATS("repeats"),
	VOLUME("volume"),
	RATIO("ratio"),
	SECONDS("seconds"),
	LOGIC("logic"),
	COUNT("count"),
	TRACK("track"),
	MEMBER("member"),
	POS("pos"),
	FROM_POS("fromPos"),
	TO_POS("toPos"),
	PLAYLIST_NAME_OR_ID("playlistNameOrId"),
	PLAYLIST_NAME("playlistName"),
	;

	companion object {
		fun getInstation(name: String): CommandArgument? = entries.find { it.argName == name }
	}
}
