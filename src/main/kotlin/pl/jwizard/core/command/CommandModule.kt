/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import java.io.IOException

enum class CommandModule(val moduleName: String) {
	MUSIC("music"),
	PLAYLIST("playlist"),
	VOTING("voting"),
	SETTINGS("settings"),
	;

	companion object {
		fun checkContractWithApi(modules: Map<String, String>) {
			if (entries.map { it.moduleName } != modules.keys.toList()) {
				throw IOException("Contract between modules is not persisted. Check CommandModule implementation")
			}
		}
	}
}
