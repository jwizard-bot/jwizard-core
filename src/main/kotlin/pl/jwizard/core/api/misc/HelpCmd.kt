/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.misc

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.BotUtils
import java.util.*

@CommandListenerBean(id = BotCommand.HELP)
class HelpCmd(
	botConfiguration: BotConfiguration,
) : AbstractCompositeCmd(
	botConfiguration
) {
	override fun execute(event: CompoundCommandEvent) {
		val messageEmbed = createEmbedMessage(event, getAvailableGuildCommands(event).size)
		val pageableData = createFormattedCommands(event)

		val paginator = createDefaultPaginator(pageableData)
		event.appendEmbedMessage(messageEmbed) { paginator.display(event.textChannel) }
	}

	private fun createFormattedCommands(event: CompoundCommandEvent): MutableList<String> {
		val commands = mutableListOf<String>()
		val commandModules = commandReflectLoader.getCommandModules()
		val botCommands = commandReflectLoader.getBotCommands()

		val prefix = if (event.slashCommandEvent == null) event.legacyPrefix else "/"
		val enabledCommands = getAvailableGuildCommands(event)

		for ((_, moduleData) in commandModules) {
			val categoryCommands = botCommands
				.filter { (name, data) -> data.module == name && enabledCommands.contains(name) }
			if (categoryCommands.isEmpty()) {
				continue
			}
			commands.add("**${BotUtils.getLang(event.lang, moduleData.name).uppercase()}**\n")
			for ((commandKey, commandData) in categoryCommands) {
				val joiner = StringJoiner("")
				joiner.add("`")
				joiner.add(prefix)
				joiner.add(commandKey)
				if (event.slashCommandEvent == null) {
					joiner.add(" [$prefix${commandData.alias}]")
				}
				val descTranslations: Map<String, String> = commandData.argsDesc.entries
					.filter { it.value != null }
					.associate { (key, value) -> key to value as String }
				if (descTranslations.size == commandData.argsDesc.size) {
					joiner.add(" ${BotUtils.getLang(event.lang, descTranslations)}")
				}
				joiner.add("`\n")
				joiner.add(BotUtils.getLang(event.lang, commandData.commandDesc))
				joiner.add("\n")

				commands.add(joiner.toString())
			}
		}
		return commands
	}

	private fun getAvailableGuildCommands(event: CompoundCommandEvent): List<String> {
		return commandsSupplier.fetchEnabledGuildCommands(event.guildDbId, event.slashCommandEvent != null)
	}

	private fun createEmbedMessage(
		event: CompoundCommandEvent,
		countOfCommands: Int,
	): MessageEmbed = CustomEmbedBuilder(botConfiguration, event)
		.addAuthor()
		.addDescription(
			placeholder = I18nResLocale.COUNT_OF_AVAIALBLE_COMMANDS,
			params = mapOf("countOfCmds" to countOfCommands),
		)
		.addColor(EmbedColor.WHITE)
		.build()
}
