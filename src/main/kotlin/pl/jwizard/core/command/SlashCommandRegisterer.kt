/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component
import pl.jwizard.core.bot.properties.BotProperties
import pl.jwizard.core.command.reflect.CommandDetailsDto
import pl.jwizard.core.command.reflect.CommandReflectLoader
import pl.jwizard.core.db.GuildSettingsSupplier
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nService
import pl.jwizard.core.log.AbstractLoggingBean
import pl.jwizard.core.util.BotUtils
import pl.jwizard.core.util.Formatter

@Component
class SlashCommandRegisterer(
	private val guildSettingsSupplier: GuildSettingsSupplier,
	private val commandReflectLoader: CommandReflectLoader,
	private val i18nService: I18nService,
	private val botProperties: BotProperties,
) : AbstractLoggingBean(SlashCommandRegisterer::class) {

	companion object {
		private const val MAX_CHOICES = 25
	}

	fun registerGuildCommands(guild: Guild) {
		val guildSettings = guildSettingsSupplier.fetchGuildCommandProperties(guild.id) ?: return
		if (!guildSettings.slashEnabled) {
			return
		}
		val commandsData = commandReflectLoader.getBotCommands()
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
			.complete()
	}

	fun mapToCommandData(name: String, details: CommandDetailsDto, lang: String): CommandData {
		val commandData = Commands.slash(name, BotUtils.getLang(lang, details.commandDesc))
		commandData.addOptions(details.args.map {
			val type = OptionType.valueOf(it.type)
			val commandOption = OptionData(
				type,
				BotUtils.getLang(lang, it.argDesc),
				i18nService.getMessage(
					if (it.req) I18nMiscLocale.REQUIRED else I18nMiscLocale.OPTIONAL,
					lang
				),
				it.req,
				type.canSupportChoices(),
			)
			if (!type.canSupportChoices() && it.options.isNotEmpty()) {
				commandOption.addChoices(it.options.map { option ->
					Choice(
						BotUtils.getLang(lang, option.desc),
						option.rawValue
					)
				})
			}
			commandOption
		})
		return commandData
	}

	fun onAutocompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
		// get command, if not found return empty choices
		val command = commandReflectLoader.getBotCommand(event.name) ?: return event.replyChoices(listOf()).queue()

		val argName = event.interaction.focusedOption.name
		val arg = command.args.find { it.argDesc.containsValue(argName) }
			?: return event.replyChoices(listOf()).queue()

		// deduct language based passed argument name, if not found assign default language
		val lang = arg.argDesc.entries.firstOrNull { it.value == argName }?.key ?: botProperties.instance.defaultLanguage

		val parsedChoices = arg.options
			.filter { it.rawValue.startsWith(event.focusedOption.value) }
			.map { Choice(BotUtils.getLang(lang, it.desc), it.rawValue) }
			.take(MAX_CHOICES)

		event.replyChoices(parsedChoices).queue()
	}
}
