/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.slash

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.Command
import pl.jwizard.jwc.command.CommandsProxyStoreBean
import pl.jwizard.jwc.command.reflect.CommandArgumentDetails
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.jda.event.JdaEventListenerBean
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean

/**
 * A listener for handling slash command autocomplete interactions in Discord.
 *
 * This class processes autocomplete events and suggests possible options based on user input for command arguments.
 *
 * @property commandsProxyStoreBean Provides access to stored command details for lookups.
 * @property i18nBean Handles internationalization to localize command argument options.
 * @property environmentBean Provides access to bot environment properties for configuration.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class SlashAutocompleteEventBean(
	private val commandsProxyStoreBean: CommandsProxyStoreBean,
	private val i18nBean: I18nBean,
	private val environmentBean: EnvironmentBean,
) : ListenerAdapter() {

	/**
	 * The maximum number of options to suggest in the autocomplete response.
	 */
	private val maxOptions = environmentBean.getProperty<Int>(BotProperty.JDA_INTERACTION_SLASH_AUTOCOMPLETE_MAX_OPTIONS)

	/**
	 * Handles the [CommandAutoCompleteInteractionEvent] triggered when a user types in an argument for a slash command.
	 * It retrieves relevant options based on the current input and responds with a list of choices.
	 *
	 * @param event The autocomplete interaction event containing user input and context.
	 */
	override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
		val command = commandsProxyStoreBean.commands[event.name] ?: return event.composeEmptyList()
		val argName = event.interaction.focusedOption.name

		val result = command.args.asSequence()
			.flatMap { it.composeCommandArgumentTags() }
			.find { (_, i18nKey, _) -> i18nKey == argName }

		val (lang, commandArg) = result
			?.let { (languageTag, _, arg) -> languageTag to arg }
			?: return event.composeEmptyList()

		val parsedChoices = commandArg.options
			.filter { it.startsWith(event.focusedOption.value) }
			.map { Command.Choice(i18nBean.tRaw(I18nDynamicMod.ARG_OPTION_MOD, arrayOf(event.name, it), lang), it) }
			.take(maxOptions)

		event.replyChoices(parsedChoices).queue()
	}

	/**
	 * Generates a sequence of command argument tags for localization and returns them as a tuple of language tag, i18n
	 * key, and the command argument details.
	 *
	 * @return A sequence of triples containing language tag, i18n key, and command argument.
	 */
	private fun CommandArgumentDetails.composeCommandArgumentTags() = i18nBean
		.tRaw(I18nDynamicMod.ARGS_MOD, arrayOf(name))
		.map { (languageTag, i18nKey) -> Triple(languageTag, i18nKey, this) }

	/**
	 * Sends an empty list of choices as a response to the event, indicating no valid autocomplete options were found.
	 */
	private fun CommandAutoCompleteInteractionEvent.composeEmptyList() = replyChoices(listOf()).queue()
}
