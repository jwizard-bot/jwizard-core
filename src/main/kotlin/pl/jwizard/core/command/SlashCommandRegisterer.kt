/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.springframework.stereotype.Component
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nService
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.settings.GuildSettingsFacade
import pl.jwizard.core.util.BotUtils
import pl.jwizard.core.util.Formatter

@Component
class SlashCommandRegisterer(
	private val guildSettingsSupplier: GuildSettingsSupplier,
	private val guildSettingsFacade: GuildSettingsFacade,
	private val i18nService: I18nService,
) : AbstractLoggingBean(SlashCommandRegisterer::class) {

	fun registerGuildCommands(guild: Guild) {
		val guildSettings = guildSettingsSupplier.fetchGuildCommandProperties(guild.id) ?: return
		if (!guildSettings.slashEnabled) {
			return
		}
		val commandsData = guildSettingsFacade.getEnabledGuildSlashCommands(guildSettings)
		val parsedSlashCommands = commandsData
			.map { (name, details) -> mapToCommandData(name, details, guildSettings.lang) }
		log.info(
			"Successfully mapped and loaded {} slash of {} commands for guild: {} ({} disabled)",
			parsedSlashCommands.size,
			commandsData.size,
			Formatter.guildTag(guild),
			commandsData.size - parsedSlashCommands.size
		)
		guild.updateCommands()
			.addCommands(parsedSlashCommands)
			.queue()
	}

	fun mapToCommandData(name: String, details: CommandDetailsDto, lang: String): CommandData {
		val commandData = CommandData(name, BotUtils.getLang(lang, details.commandDesc))
		details.args.forEach {
			commandData.addOption(
				OptionType.valueOf(it.type),
				BotUtils.getLang(lang, it.argDesc),
				i18nService.getMessage(
					if (it.req) I18nMiscLocale.REQUIRED else I18nMiscLocale.OPTIONAL,
					lang
				),
				it.req
			)
		}
		return commandData
	}
}
