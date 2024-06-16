/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

enum class CommandModule(val moduleName: String) {
	MUSIC("music"),
	RADIO_STATION("radio"),
	DJ("dj"),
	PLAYLIST("playlist"),
	VOTE("vote"),
	OTHER("other"),
	;

	companion object {
		fun getAllModuleNames(): List<String> = entries.map { it.moduleName }
	}
}