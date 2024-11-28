/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception

import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import pl.jwizard.jwc.core.config.spi.VcsDeploymentSupplier
import pl.jwizard.jwc.core.i18n.source.I18nActionSource
import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.jda.embed.MessageEmbedBuilder
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.util.mdLink
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.i18n.I18nLocaleSource
import pl.jwizard.jwl.i18n.source.I18nExceptionSource
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent
import pl.jwizard.jwl.vcs.VcsConfigBean
import pl.jwizard.jwl.vcs.VcsRepository
import java.util.*

/**
 * Manages exception tracking for the application, providing functionality to load trackers and create messages or links
 * associated with exceptions.
 *
 * This class interacts with external sources to retrieve exception data, and formats messages for Discord embeds when
 * exceptions occur.
 *
 * @property environment Provides access to application properties.
 * @property i18n Manages internationalization for exception messages.
 * @property jdaColorsCache Provides color settings for JDA embeds.
 * @property vcsDeploymentSupplier Supplies version information from version control.
 * @property vcsConfig Creates URLs for specific snapshots in the version control system.
 * @author Miłosz Gilga
 */
@SingletonComponent
class ExceptionTrackerHandlerBean(
	private val environment: EnvironmentBean,
	private val i18n: I18nBean,
	private val jdaColorsCache: JdaColorsCacheBean,
	private val vcsDeploymentSupplier: VcsDeploymentSupplier,
	private val vcsConfig: VcsConfigBean,
) {

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
	fun createTrackerMessage(
		i18nSource: I18nExceptionSource,
		context: CommandBaseContext? = null,
		args: Map<String, Any?> = emptyMap(),
	): MessageEmbed {
		val repository = VcsRepository.JWIZARD_CORE
		val deploymentVersion = vcsDeploymentSupplier.getDeploymentVersion(vcsConfig.getRepositoryName(repository))
		val (name, url) = vcsConfig.createSnapshotUrl(repository, deploymentVersion)

		val tracker = i18nSource.tracker
		val lang = context?.guildLanguage

		val stringJoiner = StringJoiner("")
		stringJoiner.addKeyValue(I18nUtilSource.BUG_TRACKER, mdLink(tracker, createTrackerUrl(tracker)), lang)
		stringJoiner.add("\n")
		stringJoiner.addKeyValue(I18nUtilSource.COMPILATION_VERSION, if (url != null) mdLink(name, url) else name, lang)

		return MessageEmbedBuilder(i18n, jdaColorsCache, context)
			.setDescription(i18nSource, args)
			.appendDescription(stringJoiner.toString())
			.setColor(JdaColor.ERROR)
			.build()
	}

	/**
	 * Creates a tracker message embed for a specific [CommandPipelineException]. This overload uses the exception's
	 * internal properties to construct the message.
	 *
	 * @param ex The [CommandPipelineException] containing the necessary information for the tracker message.
	 * @return A MessageEmbed formatted for the exception.
	 */
	fun createTrackerMessage(ex: CommandPipelineException) =
		createTrackerMessage(ex.i18nExceptionSource, ex.commandBaseContext, ex.args)

	/**
	 * Creates a link action row for the specified internationalization source, allowing users to view exception details.
	 * The link is constructed using the extracted tracker and a base URL defined in the application properties.
	 *
	 * @param i18nSource The source for internationalization to create a descriptive button label.
	 * @param context The context of the command execution, which may include localization information (optional).
	 * @return An ActionRow containing a button that links to the exception details.
	 */
	fun createTrackerLink(i18nSource: I18nExceptionSource, context: CommandBaseContext? = null): ActionRow {
		val detailsMessage = i18n.t(I18nActionSource.DETAILS, context?.guildLanguage)
		return ActionRow.of(Button.link(createTrackerUrl(i18nSource.tracker), detailsMessage))
	}

	/**
	 * Creates a link action row for a specific [CommandPipelineException]. This overload uses the exception's internal
	 * properties to construct the link.
	 *
	 * @param ex The [CommandPipelineException] containing the necessary information for the tracker link.
	 * @return An ActionRow containing a button that links to the exception details.
	 */
	fun createTrackerLink(ex: CommandPipelineException) =
		createTrackerLink(ex.i18nExceptionSource, ex.commandBaseContext)

	/**
	 * Creates the full URL for a specific exception tracker by formatting it with a base URL and the tracker ID.
	 *
	 * @param tracker The tracker ID associated with a particular exception.
	 * @return A full URL string that links to the tracker details.
	 */
	private fun createTrackerUrl(tracker: Int): String {
		val baseUrl = environment.getProperty<String>(BotProperty.SERVICE_FRONT_URL)
		val urlReferTemplate = environment.getProperty<String>(BotProperty.LINK_FRAGMENT_ERROR_CODE)
		return urlReferTemplate.format(baseUrl, tracker)
	}

	/**
	 * Helper function to add a key-value pair to a StringJoiner. Primarily used to format key-value pairs for embedding
	 * in exception messages.
	 *
	 * @param key The internationalization key source.
	 * @param value The value associated with the key.
	 * @param lang The language code for localization.
	 * @return The updated StringJoiner with the key-value pair added.
	 */
	private fun StringJoiner.addKeyValue(
		key: I18nLocaleSource,
		value: Any?,
		lang: String?,
	): StringJoiner {
		add(i18n.t(key, lang))
		add(": ")
		add(value.toString())
		return this
	}
}
