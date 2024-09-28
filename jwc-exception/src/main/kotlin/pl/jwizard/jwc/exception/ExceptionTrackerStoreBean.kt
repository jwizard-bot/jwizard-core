/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.exception.CommandPipelineException
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerStore
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.integrity.ReferentialIntegrityChecker
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.util.logger
import pl.jwizard.jwc.exception.spi.ExceptionSupplier
import java.util.*

/**
 * Manages exception tracking for the application, providing functionality to load trackers and create messages or links
 * associated with exceptions.
 *
 * This class interacts with external sources to retrieve exception data, and formats messages for Discord embeds when
 * exceptions occur.
 *
 * @property exceptionSupplier The source of exception tracking data.
 * @property environmentBean Provides access to application properties.
 * @property i18nBean Manages internationalization for exception messages.
 * @property jdaColorStoreBean Provides color settings for JDA embeds.
 * @author Miłosz Gilga
 */
@Component
class ExceptionTrackerStoreBean(
	private val exceptionSupplier: ExceptionSupplier,
	private val environmentBean: EnvironmentBean,
	private val i18nBean: I18nBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
) : ExceptionTrackerStore {

	companion object {
		private val log = logger<ExceptionTrackerStoreBean>()

		/**
		 * Pattern used to extract the last word from a string.
		 */
		private val I18N_REFER_PATTERN = Regex("\\w+\$")
	}

	/**
	 * A map to hold tracker keys and their associated count.
	 */
	private val trackers: MutableMap<String, Int> = mutableMapOf()

	/**
	 * Loads exception trackers from the data source.
	 *
	 * It divides the tracker counts by a segment size defined in the application properties and logs the number of
	 * trackers and segments loaded.
	 */
	override fun initTrackers() {
		val segmentSize = environmentBean.getProperty<Int>(BotProperty.JDA_EXCEPTION_SEGMENT_SIZE)

		val fetchedTrackers = exceptionSupplier.loadTrackers()
		ReferentialIntegrityChecker.checkIntegrity<I18nExceptionSource>(this::class, fetchedTrackers.keys)
		trackers.putAll(fetchedTrackers)

		val segments = trackers.map { it.value / segmentSize }.distinct()
		log.info("Load: {} exception trackers with: {} segments.", trackers.size, segments.size)
	}

	/**
	 * Creates a formatted message embed for the given internationalization source, including tracker details.
	 *
	 * This method retrieves the tracker associated with the provided `i18nSource`, formats it with the current
	 * application build version, and constructs a message embed to be sent in Discord.
	 *
	 * @param i18nSource The source for internationalization to create a descriptive message.
	 * @param variables A map of variables to be included in the message (optional).
	 * @param context The context of the command execution, which may include localization information (optional).
	 * @return A MessageEmbed containing the formatted message.
	 */
	override fun createTrackerMessage(
		i18nSource: I18nExceptionSource,
		variables: Map<String, Any?>,
		context: CommandBaseContext?,
	): MessageEmbed {
		val tracker = extractTracker(i18nSource)
		val buildVersion = environmentBean.getProperty<String>(BotProperty.DEPLOYMENT_BUILD_VERSION)
		val stringJoiner = StringJoiner("")
		if (tracker != null) {
			val lang = context?.guildLanguage
			stringJoiner.addKeyValue(I18nUtilSource.BUG_TRACKER, tracker, lang)
			stringJoiner.add("\n")
			stringJoiner.addKeyValue(I18nUtilSource.COMPILATION_VERSION, buildVersion, lang)
		}
		return MessageEmbedBuilder(context, i18nBean, jdaColorStoreBean)
			.setDescription(i18nSource, variables)
			.appendDescription(stringJoiner.toString())
			.setColor(JdaColor.ERROR)
			.build()
	}

	/**
	 * Creates a tracker message embed for a specific CommandPipelineException. This overload uses the exception's
	 * internal properties to construct the message.
	 *
	 * @param ex The CommandPipelineException containing the necessary information for the tracker message.
	 * @return A MessageEmbed formatted for the exception.
	 */
	override fun createTrackerMessage(ex: CommandPipelineException) =
		createTrackerMessage(ex.i18nExceptionSource, ex.variables, ex.commandBaseContext)

	/**
	 * Creates a link action row for the specified internationalization source, allowing users to view exception details.
	 * The link is constructed using the extracted tracker and a base URL defined in the application properties.
	 *
	 * @param i18nSource The source for internationalization to create a descriptive button label.
	 * @param context The context of the command execution, which may include localization information (optional).
	 * @return An ActionRow containing a button that links to the exception details.
	 */
	override fun createTrackerLink(i18nSource: I18nExceptionSource, context: CommandBaseContext?): ActionRow {
		val tracker = extractTracker(i18nSource)
		val baseUrl = environmentBean.getProperty<String>(BotProperty.SERVICE_FRONT_URL)
		val trackerUrl = if (tracker != null) {
			val urlReferTemplate = environmentBean.getProperty<String>(BotProperty.JDA_EXCEPTION_URL_REFER_TEMPLATE)
			urlReferTemplate.format(baseUrl, tracker)
		} else {
			baseUrl
		}
		val detailsMessage = i18nBean.t(I18nActionSource.DETAILS, context?.guildLanguage)
		return ActionRow.of(Button.link(trackerUrl, detailsMessage))
	}

	/**
	 * Creates a link action row for a specific CommandPipelineException. This overload uses the exception's internal
	 * properties to construct the link.
	 *
	 * @param ex The CommandPipelineException containing the necessary information for the tracker link.
	 * @return An ActionRow containing a button that links to the exception details.
	 */
	override fun createTrackerLink(ex: CommandPipelineException) =
		createTrackerLink(ex.i18nExceptionSource, ex.commandBaseContext)

	/**
	 * Extracts the tracker number associated with a given internationalization exception source. This method retrieves
	 * the last word from the `i18nExceptionSource` placeholder and looks it up in the internal trackers map.
	 *
	 * @param i18nExceptionSource The internationalization source from which to extract the tracker key.
	 * @return The associated tracker count, or null if not found.
	 */
	private fun extractTracker(i18nExceptionSource: I18nExceptionSource): Int? {
		val trackerKey = I18N_REFER_PATTERN.find(i18nExceptionSource.placeholder)?.value
		return trackers[trackerKey]
	}

	/**
	 * Adds a key-value pair to the [StringJoiner] for formatting.
	 *
	 * @param key The key to be translated and added.
	 * @param value The value associated with the key.
	 * @param lang The language for translation.
	 * @return The updated StringJoiner.
	 */
	private fun StringJoiner.addKeyValue(key: I18nLocaleSource, value: Any, lang: String?): StringJoiner {
		add(i18nBean.t(key, lang))
		add(":")
		add("`$value`")
		return this
	}
}
