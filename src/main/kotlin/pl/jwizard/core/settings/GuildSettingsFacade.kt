/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.settings

import org.springframework.stereotype.Component
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.command.reflect.CommandReflectLoader
import pl.jwizard.core.db.CommandsSupplier
import pl.jwizard.core.db.GuildCommandPropertiesDto
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.log.AbstractLoggingBean

@Component
class GuildSettingsFacade(
	private val commandReflectLoader: CommandReflectLoader,
	private val commandsSupplier: CommandsSupplier,
	private val settingsSupplier: GuildSettingsSupplier,
) : AbstractLoggingBean(GuildSettingsFacade::class) {

	fun checkIfCommandIsEnabled(commandName: String, guildId: Long): Boolean =
		checkIfCommandIsEnabled(commandName, guildId, false)

	fun checkIfSlashCommandIsEnabled(commandName: String, guildId: Long): Boolean =
		checkIfCommandIsEnabled(commandName, guildId, true)

	fun getEnabledGuildSlashCommands(properties: GuildCommandPropertiesDto): Map<String, CommandDetailsDto> {
		val enabledCommands = commandsSupplier.fetchEnabledGuildCommands(properties.id, true)
		return commandReflectLoader.getBotCommands().filterKeys { it in enabledCommands }
	}

	private fun checkIfCommandIsEnabled(commandName: String, guildId: Long, isSlashCommand: Boolean): Boolean {
		val command = commandReflectLoader.getBotCommands()[commandName] ?: return false
		return settingsSupplier.checkIfCommandIsEnabled(guildId, command.id, isSlashCommand)
	}
}