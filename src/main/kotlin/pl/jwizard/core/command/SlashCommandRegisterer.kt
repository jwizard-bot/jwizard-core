/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.command.reflect.CommandLoader
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nService
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.settings.GuildSettings
import pl.jwizard.core.util.Formatter
import org.springframework.stereotype.Component
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

@Component
class SlashCommandRegisterer(
	private val guildSettings: GuildSettings,
	private val commandLoader: CommandLoader,
	private val i18nService: I18nService,
) : AbstractLoggingBean(SlashCommandRegisterer::class) {

	fun registerGuildCommands(guild: Guild) {
		val guildSettings = guildSettings.getGuildProperties(guild.id)
		if (!guildSettings.slashEnabled) {
			return
		}
		val commandsData = commandLoader.getCommandsBaseLang(guildSettings.locale)
		val parsedSlashCommands = commandsData.commmands
			.filter {
				guildSettings.enabledCommands.contains(it.key) && guildSettings.enabledSlashCommands.contains(it.key)
			}
			.map { (name, details) -> mapToCommandData(name, details, guild.id) }
		log.info(
			"Successfully mapped and loaded {} slash of {} commands for guild: {} ({} disabled)",
			parsedSlashCommands.size,
			commandsData.commmands.size,
			Formatter.guildTag(guild),
			commandsData.commmands.size - parsedSlashCommands.size
		)
		guild.updateCommands()
			.addCommands(parsedSlashCommands)
			.queue()
	}

	fun mapToCommandData(name: String, details: CommandDetailsDto, guildId: String): CommandData {
		val commandData = CommandData(name, details.desc)
		details.args.forEach {
			commandData.addOption(
				OptionType.valueOf(it.type),
				it.name,
				i18nService.getMessage(
					if (it.req) I18nMiscLocale.REQUIRED else I18nMiscLocale.OPTIONAL,
					guildId
				),
				it.req
			)
		}
		return commandData
	}
}
