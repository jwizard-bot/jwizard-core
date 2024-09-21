/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.exception

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.exception.spi.ExceptionSupplier
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.integrity.ReferentialIntegrityChecker
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
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
class ExceptionTrackerStore(
	private val exceptionSupplier: ExceptionSupplier,
	private val environmentBean: EnvironmentBean,
	private val i18nBean: I18nBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
) {

	companion object {
		private val log = LoggerFactory.getLogger(ExceptionTrackerStore::class.java)

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
	fun loadTrackers() {
		val segmentSize = environmentBean.getProperty<Int>(BotProperty.JDA_EXCEPTION_SEGMENT_SIZE)

		val fetchedTrackers = exceptionSupplier.loadTrackers()
		ReferentialIntegrityChecker.checkIntegrity<I18nExceptionSource>(this::class, fetchedTrackers.keys)
		trackers.putAll(fetchedTrackers)

		val segments = trackers.map { it.value / segmentSize }.distinct()
		log.info("Load: {} exception trackers with: {} segments.", trackers.size, segments.size)
	}

	/**
	 * Creates a Discord embed message for the given exception.
	 *
	 * The embed includes a tracker ID (if available), the build version, and the localized description of the exception.
	 *
	 * @param ex The exception for which the message is created.
	 * @return A MessageEmbed containing the formatted exception information.
	 */
	fun createTrackerMessage(ex: CommandPipelineException): MessageEmbed {
		val tracker = extractTracker(ex)
		val buildVersion = environmentBean.getProperty<String>(BotProperty.DEPLOYMENT_BUILD_VERSION)
		val stringJoiner = StringJoiner("")
		if (tracker != null) {
			val lang = ex.commandBaseContext?.guildLanguage
			stringJoiner.addKeyValue(I18nUtilSource.BUG_TRACKER, tracker, lang)
			stringJoiner.add("\n")
			stringJoiner.addKeyValue(I18nUtilSource.COMPILATION_VERSION, buildVersion, lang)
		}
		return MessageEmbedBuilder(ex.commandBaseContext, i18nBean, jdaColorStoreBean)
			.setDescription(ex.i18nExceptionSource, ex.variables)
			.appendDescription(stringJoiner.toString())
			.setColor(JdaColor.ERROR)
			.build()
	}

	/**
	 * Creates a button link to the tracker associated with the exception.
	 *
	 * The link directs users to the service front URL, optionally including the tracker ID if available.
	 *
	 * @param ex The exception for which the tracker link is created.
	 * @return An ActionRow containing a button with the tracker link.
	 */
	fun createTrackerLink(ex: CommandPipelineException): ActionRow {
		val tracker = extractTracker(ex)
		val baseUrl = environmentBean.getProperty<String>(BotProperty.SERVICE_FRONT_URL)
		val trackerUrl = if (tracker != null) {
			val urlReferTemplate = environmentBean.getProperty<String>(BotProperty.JDA_EXCEPTION_URL_REFER_TEMPLATE)
			urlReferTemplate.format(baseUrl, tracker)
		} else {
			baseUrl
		}
		val detailsMessage = i18nBean.t(I18nActionSource.DETAILS, ex.commandBaseContext?.guildLanguage)
		return ActionRow.of(Button.link(trackerUrl, detailsMessage))
	}

	/**
	 * Extracts the tracker ID from the exception's source.
	 *
	 * @param ex The exception from which to extract the tracker.
	 * @return The tracker ID, or null if not found.
	 */
	private fun extractTracker(ex: CommandPipelineException): Int? {
		val trackerKey = I18N_REFER_PATTERN.find(ex.i18nExceptionSource.placeholder)?.value
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
