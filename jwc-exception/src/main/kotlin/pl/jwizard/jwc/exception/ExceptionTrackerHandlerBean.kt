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
import pl.jwizard.jwc.core.exception.spi.ExceptionTrackerHandler
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.util.mdCode
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.property.AppBaseProperty
import java.util.*

/**
 * Manages exception tracking for the application, providing functionality to load trackers and create messages or links
 * associated with exceptions.
 *
 * This class interacts with external sources to retrieve exception data, and formats messages for Discord embeds when
 * exceptions occur.
 *
 * @property environmentBean Provides access to application properties.
 * @property i18nBean Manages internationalization for exception messages.
 * @property jdaColorStoreBean Provides color settings for JDA embeds.
 * @author Miłosz Gilga
 */
@Component
class ExceptionTrackerHandlerBean(
	private val environmentBean: EnvironmentBean,
	private val i18nBean: I18nBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
) : ExceptionTrackerHandler {

	/**
	 * Creates a formatted message embed for the given internationalization source, including tracker details.
	 *
	 * This method retrieves the tracker associated with the provided `i18nSource`, formats it with the current
	 * application build version, and constructs a message embed to be sent in Discord.
	 *
	 * @param i18nSource The source for internationalization to create a descriptive message.
	 * @param context The context of the command execution, which may include localization information (optional).
	 * @param args A map of variables to be included in the message (optional).
	 * @return A MessageEmbed containing the formatted message.
	 */
	override fun createTrackerMessage(
		i18nSource: I18nExceptionSource,
		context: CommandBaseContext?,
		args: Map<String, Any?>,
	): MessageEmbed {
		val buildVersion = environmentBean.getProperty<String>(AppBaseProperty.DEPLOYMENT_BUILD_VERSION)
		val lang = context?.guildLanguage

		val stringJoiner = StringJoiner("")
		stringJoiner.addKeyValue(I18nUtilSource.BUG_TRACKER, i18nSource.tracker, lang)
		stringJoiner.add("\n")
		stringJoiner.addKeyValue(I18nUtilSource.COMPILATION_VERSION, buildVersion, lang)

		return MessageEmbedBuilder(i18nBean, jdaColorStoreBean, context)
			.setDescription(i18nSource, args)
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
		createTrackerMessage(ex.i18nExceptionSource, ex.commandBaseContext, ex.args)

	/**
	 * Creates a link action row for the specified internationalization source, allowing users to view exception details.
	 * The link is constructed using the extracted tracker and a base URL defined in the application properties.
	 *
	 * @param i18nSource The source for internationalization to create a descriptive button label.
	 * @param context The context of the command execution, which may include localization information (optional).
	 * @return An ActionRow containing a button that links to the exception details.
	 */
	override fun createTrackerLink(i18nSource: I18nExceptionSource, context: CommandBaseContext?): ActionRow {
		val baseUrl = environmentBean.getProperty<String>(BotProperty.SERVICE_FRONT_URL)
		val urlReferTemplate = environmentBean.getProperty<String>(BotProperty.JDA_EXCEPTION_URL_REFER_TEMPLATE)
		val trackerUrl = urlReferTemplate.format(baseUrl, i18nSource.tracker)
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
		add(mdCode(value.toString()))
		return this
	}
}
