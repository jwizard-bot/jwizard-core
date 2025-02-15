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

@SingletonComponent
internal class SlashCommandRegistererBean(
	private val i18n: I18nBean,
	private val environment: EnvironmentBean,
	private val commandsCache: CommandsCacheBean,
) : SlashCommandRegisterer {
	companion object {
		private val log = logger<SlashCommandRegistererBean>()

		// use for hierarchical command levels
		private const val LEVEL_SEPARATOR = "."
	}

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
				subcommandGroup.addSubcommands(
					*(appliedSubcommands(
						subcommands,
						defaultLanguage,
						restLanguages
					))
				)
				slash.addSubcommandGroups(subcommandGroup)
			}
			slash.addSubcommands(
				*appliedSubcommands(
					allSingleLevelGroups,
					defaultLanguage,
					restLanguages
				)
			)
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

	private fun appliedSubcommands(
		subcommands: List<Pair<String, Command>>,
		defaultLanguage: String,
		restLanguages: List<String>,
	) = subcommands
		.map {
			val (subcommand, command) = it
			SubcommandData(subcommand, i18n.t(command, defaultLanguage))
				.addOptions(*createCommandOptions(command, defaultLanguage, restLanguages))
				.setDescriptionLocalizations(mapToLanguagesContent(command, restLanguages))
		}
		.toTypedArray()

	private fun createCommandOptions(
		command: Command,
		defaultLanguage: String,
		restLanguages: List<String>,
	) = command.exactArguments
		.map {
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
			commandOption.setDescriptionLocalizations(
				mapToLanguagesContent(
					i18nRequired,
					restLanguages
				)
			)
			if (!type.canSupportChoices() && it.options.isNotEmpty()) {
				commandOption.addChoices(it.options.map { option ->
					Choice(i18n.t(option, defaultLanguage), option.textKey)
						.setNameLocalizations(mapToLanguagesContent(option, restLanguages))
				})
			}
			commandOption
		}
		.toTypedArray()

	private fun mapToLanguagesContent(
		i18nSource: I18nLocaleSource,
		restLanguages: List<String>,
	) = restLanguages.associate {
		DiscordLocale.from(Locale.forLanguageTag(it)) to i18n.t(
			i18nSource,
			it,
		)
	}
}
