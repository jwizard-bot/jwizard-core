/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.command.reflect.ModuleDetailsDto

interface CommandsSupplier {
	fun fetchAllModules(): Map<String, ModuleDetailsDto>
	fun fetchAllCommands(): Map<String, CommandDetailsDto>
	fun checkIfModuleIsEnabled(moduleName: String, guildId: Long): Boolean
	fun fetchEnabledGuildCommands(guildId: Long, isSlashCommands: Boolean): List<String>
}