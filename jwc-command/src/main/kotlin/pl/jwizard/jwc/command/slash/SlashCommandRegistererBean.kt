/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.slash

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import org.springframework.stereotype.Component
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.spi.SlashCommandRegisterer
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.util.logger

/**
 * A Spring component responsible for registering slash commands in Discord guilds. It implements the
 * [SlashCommandRegisterer] interface and interacts with the JDA API to create and update slash commands based on the
 * application's command configuration.
 *
 * @property i18nBean Provides internationalization functionality to localize command names and descriptions.
 * @property environmentBean Provides access to environment-specific properties for the guild.
 * @property commandsCacheBean Stores command instances and their details for reflection-based registration.
 * @author Miłosz Gilga
 */
@Component
class SlashCommandRegistererBean(
	private val i18nBean: I18nBean,
	private val environmentBean: EnvironmentBean,
	private val commandsCacheBean: CommandsCacheBean,
) : SlashCommandRegisterer {

	companion object {
		private val log = logger<SlashCommandRegistererBean>()
	}

	/**
	 * Registers slash commands for a given guild. It retrieves the guild's command properties and checks if slash
	 * commands are enabled. If enabled, it maps the commands to JDA's [CommandData] objects and registers them with the
	 * guild.
	 *
	 * @param guild The Discord guild for which to register the slash commands.
	 */
	override fun registerGuildCommands(guild: Guild) {
		val properties = environmentBean.getGuildMultipleProperties(
			guildProperties = listOf(
				GuildProperty.SLASH_ENABLED,
				GuildProperty.LANGUAGE_TAG,
			),
			guildId = guild.idLong
		)
		val lang = properties.getProperty<String>(GuildProperty.LANGUAGE_TAG)
		if (!properties.getProperty<Boolean>(GuildProperty.SLASH_ENABLED)) {
			return
		}
		val loadedCommands = commandsCacheBean.instancesContainer.map { it.key }
		val commands = Command.entries
		val parsedSlashCommands = commands
			.filter { loadedCommands.contains(it) }
			.map { details -> mapToCommandData(details, lang) }
		log.info(
			"Load: {} slash of: {} commands for guild: {} (disabled: {}).",
			parsedSlashCommands.size,
			commands.size,
			guild.qualifier,
			commands.size - parsedSlashCommands.size
		)
		guild.jda.updateCommands().queue()
		guild.updateCommands()
			.addCommands(parsedSlashCommands)
			.queue()
	}

	/**
	 * Maps a command and its details to a [Command], which is used by JDA to create slash commands. It also configures
	 * the options (arguments) of the command, including their types, localization, and whether they support choices.
	 *
	 * @param details The [Command] containing information about the command's arguments and options.
	 * @param lang The language to use for localizing the command's name and arguments.
	 * @return A [CommandData] object representing the command, ready to be registered with the guild.
	 */
	private fun mapToCommandData(details: Command, lang: String): CommandData {
		val commandData = Commands.slash(details.textId, i18nBean.t(details.i18nSource, lang))
		commandData.addOptions(details.exactArguments.map {
			val type = OptionType.valueOf(it.type.name)
			val commandOption = OptionData(
				type,
				i18nBean.t(it.i18nSource, lang),
				i18nBean.t(if (it.required) I18nUtilSource.REQUIRED else I18nUtilSource.OPTIONAL, lang),
				it.required,
				type.canSupportChoices(),
			)
			if (!type.canSupportChoices() && it.options.isNotEmpty()) {
				commandOption.addChoices(it.options.map { (key, value) -> Choice(i18nBean.t(value, lang), key) })
			}
			commandOption
		})
		return commandData
	}
}
