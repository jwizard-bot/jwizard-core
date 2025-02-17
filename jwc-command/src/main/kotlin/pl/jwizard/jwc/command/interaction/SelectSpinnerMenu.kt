package pl.jwizard.jwc.command.interaction

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.LayoutComponent
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import pl.jwizard.jwc.core.i18n.source.I18nVotingSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorsCache
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.jda.event.queue.EventQueue
import pl.jwizard.jwc.core.jda.event.queue.EventQueueListener
import pl.jwizard.jwc.core.util.mdList
import pl.jwizard.jwc.core.util.toMD5
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.i18n.I18nLocaleSource
import java.util.concurrent.TimeUnit
import kotlin.random.Random

abstract class SelectSpinnerMenu<T : MenuOption>(
	private val context: CommandBaseContext,
	private val options: List<T>,
) : EventQueueListener<StringSelectInteractionEvent>, Component() {
	private lateinit var trimmedOptions: List<T>
	private lateinit var message: Message

	fun initEvent(eventQueue: EventQueue, message: Message) {
		this.message = message
		eventQueue.waitForScheduledEvent(
			StringSelectInteractionEvent::class,
			this,
			elapsedTimeSec,
			TimeUnit.SECONDS
		)
	}

	fun createMenuComponent(
		i18n: I18n,
		jdaColorsCache: JdaColorsCache,
		i18nSource: I18nLocaleSource,
		minValues: Int = 1,
		maxValues: Int = 1,
	): Pair<MessageEmbed, ActionRow> {
		trimmedOptions = options.subList(0, this.maxElementsToChoose).distinctBy { it.key }
		val args = mapOf(
			"resultsFound" to trimmedOptions.size,
			"elapsedTime" to elapsedTimeSec,
			"afterTimeResult" to i18n.t(
				if (randomChoice) I18nVotingSource.RANDOM_RESULT else I18nVotingSource.FIRST_RESULT,
				context.language
			),
		)
		val message = MessageEmbedBuilder(i18n, jdaColorsCache, context)
			.setDescription(i18nSource, args)
			.appendDescription(trimmedOptions.joinToString("") {
				mdList(
					it.formattedToEmbed,
					eol = true
				)
			})
			.setColor(JdaColor.PRIMARY)
			.build()

		val menuBuilder = StringSelectMenu
			.create(createComponentId(menuId))
			.setPlaceholder(i18n.t(I18nVotingSource.PICK_AN_OPTION, context.language))
			.setMaxValues(minValues)
			.setMinValues(maxValues)

		trimmedOptions.forEach { menuBuilder.addOption(it.key.take(100), toMD5(it.value)) }
		return Pair(message, ActionRow.of(menuBuilder.build()))
	}

	final override fun onPredicateExecuteEvent(
		event: StringSelectInteractionEvent,
	) = getComponentId(event.componentId) == menuId

	final override fun onEvent(event: StringSelectInteractionEvent) {
		event.deferEdit().queue()
		val selectedOptions = trimmedOptions.filter { event.values.contains(toMD5(it.value)) }
		onEvent(event, context, selectedOptions)
		val components = event.message.components.map(LayoutComponent::asDisabled)
		event.hook.editOriginalComponents(components).queue()
	}

	final override fun onTimeout() {
		val selectedIndex = if (randomChoice) Random.nextInt(trimmedOptions.size) else 0
		onTimeout(context, options[selectedIndex])
		message.delete().queue()
	}

	// unique identifier for this menu
	protected abstract val menuId: String

	// duration in seconds before the interaction times out
	protected abstract val elapsedTimeSec: Long

	// maximum number of options that can be selected
	protected abstract val maxElementsToChoose: Int

	// whether a random choice should be made if the user does not select any options
	protected abstract val randomChoice: Boolean

	protected abstract fun onEvent(
		event: StringSelectInteractionEvent,
		context: CommandBaseContext,
		options: List<T>,
	)

	protected abstract fun onTimeout(context: CommandBaseContext, option: T)
}
