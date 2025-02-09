/*
 * Copyright (c) 2025 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.slash

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.*
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.spi.SlashCommandRegisterer
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.property.AppBaseListProperty
import pl.jwizard.jwl.property.AppBaseProperty
import pl.jwizard.jwl.util.logger
import java.util.*

/**
 * Manages the registration of global and guild-specific slash commands for the bot.
 *
 * This class uses cached command definitions and environment configurations to dynamically register slash commands,
 * ensuring internationalization and hierarchical command structure.
 *
 * @property i18n Provides internationalization functionality to localize command names and descriptions.
 * @property environment Provides access to environment-specific properties.
 * @property commandsCache Stores command instances and their details for reflection-based registration.
 * @author Miłosz Gilga
 */
@SingletonComponent
class SlashCommandRegistererBean(
	private val i18n: I18nBean,
	private val environment: EnvironmentBean,
	private val commandsCache: CommandsCacheBean,
) : SlashCommandRegisterer {

	companion object {
		private val log = logger<SlashCommandRegistererBean>()

		/**
		 * Separator used to define hierarchical levels of commands.
		 */
		private const val LEVEL_SEPARATOR = "."
	}

	/**
	 * Registers global slash commands using the cached command definitions.
	 *
	 * @param jda The [JDA] instance used to update global commands.
	 */
	override fun registerGlobalCommands(jda: JDA) {
		val rootCommands = parseCommands(
			loadedCommands = commandsCache.globalCommandInstances.loadedSlashCommands,
			global = true,
			logCallback = { message, args -> log.info(message, *args) },
		)
		jda.updateCommands()
			.addCommands(rootCommands)
			.queue()
	}

	/**
	 * Registers guild-specific slash commands using the cached command definitions.
	 *
	 * @param guild The [Guild] instance where the commands should be registered.
	 */
	override fun registerGuildCommands(guild: Guild) {
		val rootCommands = parseCommands(
			loadedCommands = commandsCache.guildCommandInstances.loadedSlashCommands,
			global = false,
			logCallback = { message, args -> log.debug(message, *args) },
		)
		guild.updateCommands()
			.addCommands(rootCommands)
			.queue()
	}

	/**
	 * Parses and builds command data for either global or guild-specific commands.
	 *
	 * This method organizes commands into hierarchical structures (if applicable) and applies internationalized
	 * descriptions and options.
	 *
	 * @param loadedCommands The list of commands loaded from the cache.
	 * @param global Indicates whether the commands are global (`true`) or guild-specific (`false`).
	 * @param logCallback Callback function for logging the parsing process.
	 * @return A list of [CommandData] objects ready to be registered.
	 */
	private fun parseCommands(
		loadedCommands: List<Command>,
		global: Boolean,
		logCallback: (String, Array<Any>) -> Unit,
	): List<CommandData> {
		val defaultLanguage = environment.getProperty<String>(AppBaseProperty.I18N_DEFAULT_LANGUAGE)
		val restLanguages = environment.getListProperty<String>(AppBaseListProperty.I18N_LANGUAGES)

		val definedCommands = Command.entries.filter { it.global == global }
		val enabledCommands = definedCommands.filter { loadedCommands.contains(it) }
		val rootCommands = mutableListOf<CommandData>()

		val commandsGroupedByLevel = enabledCommands.groupBy(
			keySelector = { it.textKey.substringBefore(LEVEL_SEPARATOR) },
			valueTransform = { it.textKey.substringAfter(LEVEL_SEPARATOR) to it },
		)
		for ((rootCommand, levels) in commandsGroupedByLevel) {
			val slash = Commands.slash(rootCommand, rootCommand)

			val allNestedGroups = levels.filter { it.first.contains(LEVEL_SEPARATOR) }
			val allSingleLevelGroups = levels - allNestedGroups.toSet()

			if (allNestedGroups.isEmpty() && levels.size == 1) {
				val command = levels[0].second
				slash
					.setDescription(i18n.t(command, defaultLanguage))
					.setDescriptionLocalizations(mapToLanguagesContent(command, restLanguages))
					.addOptions(*createCommandOptions(command, defaultLanguage, restLanguages))
				rootCommands.add(slash)
				continue
			}
			val nestedGroupsByLevel = allNestedGroups.groupBy(
				keySelector = { it.first.substringBefore(LEVEL_SEPARATOR) },
				valueTransform = { it.first.substringAfter(LEVEL_SEPARATOR) to it.second }
			)
			for ((rootGroup, subcommands) in nestedGroupsByLevel) {
				val subcommandGroup = SubcommandGroupData(rootGroup, rootGroup)
				subcommandGroup.addSubcommands(*(appliedSubcommands(subcommands, defaultLanguage, restLanguages)))
				slash.addSubcommandGroups(subcommandGroup)
			}
			slash.addSubcommands(*appliedSubcommands(allSingleLevelGroups, defaultLanguage, restLanguages))
			rootCommands.add(slash)
		}
		logCallback(
			"Load: {} {} slash of: {} commands (only root commands: {}, disabled: {}).",
			arrayOf(
				loadedCommands.size,
				if (global) "global" else "guild",
				definedCommands.size,
				rootCommands.size,
				definedCommands.size - loadedCommands.size
			),
		)
		return rootCommands
	}

	/**
	 * Converts subcommand definitions into [SubcommandData].
	 *
	 * @param subcommands List of subcommands with their hierarchical keys and [Command] definitions.
	 * @param defaultLanguage The default language used for descriptions.
	 * @param restLanguages List of additional languages for localization.
	 * @return An array of [SubcommandData] objects.
	 */
	private fun appliedSubcommands(
		subcommands: List<Pair<String, Command>>,
		defaultLanguage: String,
		restLanguages: List<String>,
	) = subcommands.map {
		val (subcommand, command) = it
		SubcommandData(subcommand, i18n.t(command, defaultLanguage))
			.addOptions(*createCommandOptions(command, defaultLanguage, restLanguages))
			.setDescriptionLocalizations(mapToLanguagesContent(command, restLanguages))
	}.toTypedArray()

	/**
	 * Creates options for a command, applying internationalization to names and descriptions.
	 *
	 * @param command The [Command] for which options are created.
	 * @param defaultLanguage The default language used for option descriptions.
	 * @param restLanguages List of additional languages for localization.
	 * @return An array of [OptionData] objects.
	 */
	private fun createCommandOptions(command: Command, defaultLanguage: String, restLanguages: List<String>) =
		command.exactArguments.map {
			val type = OptionType.valueOf(it.type.name)
			val i18nRequired = if (it.required) {
				I18nUtilSource.REQUIRED
			} else {
				I18nUtilSource.OPTIONAL
			}
			val commandOption = OptionData(
				type,
				i18n.t(it, defaultLanguage),
				i18n.t(i18nRequired, defaultLanguage),
				it.required,
				type.canSupportChoices(),
			)
			commandOption.setNameLocalizations(mapToLanguagesContent(it, restLanguages))
			commandOption.setDescriptionLocalizations(mapToLanguagesContent(i18nRequired, restLanguages))
			if (!type.canSupportChoices() && it.options.isNotEmpty()) {
				commandOption.addChoices(it.options.map { option ->
					Choice(i18n.t(option, defaultLanguage), option.textKey)
						.setNameLocalizations(mapToLanguagesContent(option, restLanguages))
				})
			}
			commandOption
		}.toTypedArray()

	/**
	 * Maps localized descriptions to supported Discord locales.
	 *
	 * @param i18nSource The source of the localized content.
	 * @param restLanguages The list of additional languages.
	 * @return A map of [DiscordLocale] to localized descriptions.
	 */
	private fun mapToLanguagesContent(i18nSource: I18nLocaleSource, restLanguages: List<String>) =
		restLanguages.associate { DiscordLocale.from(Locale.forLanguageTag(it)) to i18n.t(i18nSource, it) }
}
