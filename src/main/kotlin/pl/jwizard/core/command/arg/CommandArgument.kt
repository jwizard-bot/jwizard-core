/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.arg

import org.apache.commons.collections.CollectionUtils

enum class CommandArgument(
	val argName: String,
) {
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

	companion object {
		fun getInstation(name: String): CommandArgument? = entries.find { it.argName == name }

		fun checkIntegrity(reference: List<String>): Boolean = CollectionUtils.isEqualCollection(
			reference,
			entries.map { it.argName }
		)
	}
}
