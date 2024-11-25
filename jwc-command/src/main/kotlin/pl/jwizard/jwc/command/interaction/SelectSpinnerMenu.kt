/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.interaction

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import org.apache.commons.codec.digest.DigestUtils
import pl.jwizard.jwc.core.i18n.source.I18nVotingSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.jda.event.queue.EventQueueBean
import pl.jwizard.jwc.core.jda.event.queue.EventQueueListener
import pl.jwizard.jwc.core.util.mdList
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.i18n.I18nLocaleSource
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Abstract class representing a selectable spinner menu for interaction.
 *
 * @param T The type of menu options that can be selected.
 * @property context The context of the command execution.
 * @property options The list of available options for selection.
 * @author Miłosz Gilga
 */
abstract class SelectSpinnerMenu<T : MenuOption>(
	private val context: CommandBaseContext,
	private val options: List<T>,
) : EventQueueListener<StringSelectInteractionEvent>, Component() {

	/**
	 * The list of trimmed options that will be displayed in the menu.
	 */
	private lateinit var trimmedOptions: List<T>

	/**
	 * The message associated with the spinner menu.
	 */
	private lateinit var message: Message

	/**
	 * Initializes the event listener for the select interaction menu. This method sets up the message and registers the
	 * event listener to wait for interactions for a specified period of time.
	 *
	 * @param eventQueueBean The event queue bean for handling events.
	 * @param message The message associated with the select spinner menu.
	 */
	fun initEvent(eventQueueBean: EventQueueBean, message: Message) {
		this.message = message
		eventQueueBean.waitForScheduledEvent(StringSelectInteractionEvent::class, this, elapsedTimeSec, TimeUnit.SECONDS)
	}

	/**
	 * Creates the menu component for the select spinner. This method constructs the embed message and the action row
	 * with the selection menu.
	 *
	 * @param i18nBean The internationalization bean for localized strings.
	 * @param jdaColorStoreBean The bean for managing colors used in JDA.
	 * @param i18nSource The source for localized description text.
	 * @param minValues The minimum number of values that can be selected.
	 * @param maxValues The maximum number of values that can be selected.
	 * @return A pair containing the embed message and the action row with the select menu.
	 */
	fun createMenuComponent(
		i18nBean: I18nBean,
		jdaColorStoreBean: JdaColorStoreBean,
		i18nSource: I18nLocaleSource,
		minValues: Int = 1,
		maxValues: Int = 1,
	): Pair<MessageEmbed, ActionRow> {
		trimmedOptions = options.subList(0, this.maxElementsToChoose).distinctBy { it.key }
		val args = mapOf(
			"resultsFound" to trimmedOptions.size,
			"elapsedTime" to elapsedTimeSec,
			"afterTimeResult" to i18nBean.t(
				if (randomChoice) I18nVotingSource.RANDOM_RESULT else I18nVotingSource.FIRST_RESULT,
				context.guildLanguage
			),
		)
		val message = MessageEmbedBuilder(i18nBean, jdaColorStoreBean, context)
			.setDescription(i18nSource, args)
			.appendDescription(trimmedOptions.joinToString("") { mdList(it.formattedToEmbed, eol = true) })
			.setColor(JdaColor.PRIMARY)
			.build()

		val menuBuilder = StringSelectMenu
			.create(createComponentId(menuId))
			.setPlaceholder(i18nBean.t(I18nVotingSource.PICK_AN_OPTION, context.guildLanguage))
			.setMaxValues(minValues)
			.setMinValues(maxValues)

		trimmedOptions.forEach { menuBuilder.addOption(it.key.take(100), DigestUtils.md2Hex(it.value)) }
		return Pair(message, ActionRow.of(menuBuilder.build()))
	}

	/**
	 * Checks if the event corresponds to this menu's component ID.
	 *
	 * @param event The interaction event to check.
	 * @return True if the event is for this menu, false otherwise.
	 */
	final override fun onPredicateExecuteEvent(event: StringSelectInteractionEvent) =
		getComponentId(event.componentId) == menuId

	/**
	 * Handles the event when an option is selected from the spinner menu. This method is called when a user selects an
	 * option, and it processes the selection and updates the message components accordingly.
	 *
	 * @param event The selection interaction event.
	 */
	final override fun onEvent(event: StringSelectInteractionEvent) {
		event.deferEdit().queue()
		val selectedOptions = trimmedOptions.filter { event.values.contains(DigestUtils.md2Hex(it.value)) }
		onEvent(event, context, selectedOptions)
		val components = event.message.components.map(LayoutComponent::asDisabled)
		event.hook.editOriginalComponents(components).queue()
	}

	/**
	 * Handles the event when the interaction times out. This method is called when the user does not make a selection
	 * within the specified time.
	 */
	final override fun onTimeout() {
		val selectedIndex = if (randomChoice) Random.nextInt(trimmedOptions.size) else 0
		onTimeout(context, options[selectedIndex])
		message.delete().queue()
	}

	/**
	 * The unique identifier for this menu.
	 */
	protected abstract val menuId: String

	/**
	 * The duration in seconds before the interaction times out.
	 */
	protected abstract val elapsedTimeSec: Long

	/**
	 * The maximum number of options that can be selected.
	 */
	protected abstract val maxElementsToChoose: Int

	/**
	 * Indicates whether a random choice should be made if the user does not select any options.
	 */
	protected abstract val randomChoice: Boolean

	/**
	 * Handles the event when a selection is made from the menu. This method should be implemented by subclasses to
	 * define what happens when an option is selected.
	 *
	 * @param event The selection interaction event.
	 * @param context The context of the command execution.
	 * @param options The list of selected options.
	 */
	protected abstract fun onEvent(
		event: StringSelectInteractionEvent,
		context: CommandBaseContext,
		options: List<T>,
	)

	/**
	 * Handles the event when the interaction times out without a selection. This method should be implemented by
	 * subclasses to define what happens when a timeout occurs.
	 *
	 * @param context The context of the command execution.
	 * @param option The option that is selected in case of a timeout.
	 */
	protected abstract fun onTimeout(context: CommandBaseContext, option: T)
}
