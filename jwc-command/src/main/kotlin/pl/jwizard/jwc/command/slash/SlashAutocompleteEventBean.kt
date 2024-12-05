/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.slash

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import pl.jwizard.jwc.command.CommandsCacheBean
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwl.command.Command
import pl.jwizard.jwl.command.arg.Argument
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.util.rawCommandToDotFormat

/**
 * A listener for handling slash command autocomplete interactions in Discord.
 *
 * This class processes autocomplete events and suggests possible options based on user input for command arguments.
 *
 * @property commandsCache Provides access to stored command details for lookups.
 * @property i18n Handles internationalization to localize command argument options.
 * @property environment Provides access to bot environment properties for configuration.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class SlashAutocompleteEventBean(
	private val commandsCache: CommandsCacheBean,
	private val i18n: I18nBean,
	private val environment: EnvironmentBean,
) : ListenerAdapter() {

	/**
	 * The maximum number of options to suggest in the autocomplete response.
	 */
	private val maxOptions = environment.getProperty<Int>(BotProperty.JDA_INTERACTION_SLASH_AUTOCOMPLETE_MAX_OPTIONS)

	/**
	 * Handles the [CommandAutoCompleteInteractionEvent] triggered when a user types in an argument for a slash command.
	 * It retrieves relevant options based on the current input and responds with a list of choices.
	 *
	 * @param event The autocomplete interaction event containing user input and context.
	 */
	override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
		val commandName = event.interaction.fullCommandName.rawCommandToDotFormat()
		val command = Command.entries.find { it.textKey == commandName } ?: return event.composeEmptyList()
		val argName = event.interaction.focusedOption.name

		val result = command.exactArguments.asSequence()
			.flatMap { it.composeCommandArgumentTags(it) }
			.find { (_, i18nKey, _) -> i18nKey == argName }

		val (lang, commandArg) = result
			?.let { (languageTag, _, arg) -> languageTag to arg }
			?: return event.composeEmptyList()

		val parsedChoices = commandArg.options
			.filter { it.textKey.startsWith(event.focusedOption.value) }
			.map { Choice(i18n.t(it, lang), it.textKey) }
			.take(maxOptions)

		event.replyChoices(parsedChoices).queue()
	}

	/**
	 * Retrieves a sequence of triples containing the language tag, i18n key, and command argument, which are used to
	 * generate localized command argument tags.
	 *
	 * @param argument
	 * @return A sequence of triples containing language tag, i18n key, and command argument.
	 */
	private fun Argument.composeCommandArgumentTags(argument: Argument) = i18n.t(argument)
		.map { (languageTag, i18nKey) -> Triple(languageTag, i18nKey, this) }

	/**
	 * Sends an empty list of choices as a response to the event, indicating no valid autocomplete options were found.
	 */
	private fun CommandAutoCompleteInteractionEvent.composeEmptyList() = replyChoices(listOf()).queue()
}
