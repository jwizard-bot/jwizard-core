/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.misc

import java.util.*
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.AbstractCompositeCmd
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import net.dv8tion.jda.api.entities.MessageEmbed

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

		val guildDetails = guildSettings.getGuildProperties(event.guildId)
		val commandDetails = commandLoader.getCommandsBaseLang(guildDetails.locale)
		val prefix = if (event.slashCommandEvent == null) guildDetails.legacyPrefix else "/"

		for ((key, translation) in commandDetails.categories) {
			val enabledCommands = getAvailableGuildCommands(event)
			val categoryCommands = commandDetails.commmands
				.filter { it.value.category == key && enabledCommands.contains(it.key) }
			if (categoryCommands.isEmpty()) {
				continue
			}
			commands.add("**${translation.uppercase()}**\n")
			for ((commandKey, commandData) in categoryCommands) {
				val joiner = StringJoiner("")
				val aliases = commandData.aliases.joinToString(", ") { prefix + it }

				joiner.add("`")
				joiner.add(prefix)
				joiner.add(commandKey)
				if (event.slashCommandEvent == null) {
					joiner.add(" [$aliases]")
				}
				if (commandData.argsDesc != null) {
					joiner.add(" ${commandData.argsDesc}")
				}
				joiner.add("`\n")
				joiner.add(commandData.desc)
				joiner.add("\n")

				commands.add(joiner.toString())
			}
		}
		return commands
	}

	private fun getAvailableGuildCommands(
		event: CompoundCommandEvent
	): List<String> {
		val guildDetails = guildSettings.getGuildProperties(event.guildId)
		return if (event.slashCommandEvent != null) {
			guildDetails.enabledSlashCommands
		} else {
			guildDetails.enabledCommands
		}
	}

	private fun createEmbedMessage(
		event: CompoundCommandEvent,
		countOfCommands: Int,
	): MessageEmbed = CustomEmbedBuilder(event, botConfiguration)
		.addAuthor()
		.addDescription(
			placeholder = I18nResLocale.COUNT_OF_AVAIALBLE_COMMANDS,
			params = mapOf("countOfCmds" to countOfCommands),
		)
		.addColor(EmbedColor.WHITE)
		.build()
}
