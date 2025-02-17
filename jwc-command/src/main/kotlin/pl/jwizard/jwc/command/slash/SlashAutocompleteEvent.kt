package pl.jwizard.jwc.command.slash

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import pl.jwizard.jwc.core.jda.event.JdaEventListener
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.property.BaseEnvironment

@JdaEventListener
internal class SlashAutocompleteEvent(
	private val i18n: I18n,
	environment: BaseEnvironment,
) : ListenerAdapter() {
	private val maxOptions = environment
		.getProperty<Int>(BotProperty.JDA_INTERACTION_SLASH_AUTOCOMPLETE_MAX_OPTIONS)

	override fun onCommandAutoCompleteInteraction(
		event: CommandAutoCompleteInteractionEvent,
	) = event.replyChoices(findCommandInteraction(event)).queue()

	private fun findCommandInteraction(
		event: CommandAutoCompleteInteractionEvent,
	): List<Choice> {
		val commandName = Command.rawCommandToDotFormat(event.interaction.fullCommandName)
		val argName = event.interaction.focusedOption.name

		val command = Command.entries
			.find { it.textKey == commandName }
			?: return emptyList()

		val result = command.exactArguments.asSequence()
			.flatMap { it.composeCommandArgumentTags(it) }
			.find { (_, i18nKey, _) -> i18nKey == argName }

		val (lang, commandArg) = result
			?.let { (languageTag, _, arg) -> languageTag to arg }
			?: return emptyList()

		return commandArg.options
			.filter { it.textKey.startsWith(event.focusedOption.value) }
			.map { Choice(i18n.t(it, lang), it.textKey) }
			.take(maxOptions)
	}

	private fun Argument.composeCommandArgumentTags(
		argument: Argument
	) = i18n.t(argument).map { (languageTag, i18nKey) -> Triple(languageTag, i18nKey, this) }
}
