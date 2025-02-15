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

@SingletonComponent
class ExceptionTrackerHandlerBean(
	private val environment: EnvironmentBean,
	private val i18n: I18nBean,
	private val jdaColorsCache: JdaColorsCacheBean,
	private val vcsDeploymentSupplier: VcsDeploymentSupplier,
	private val vcsConfig: VcsConfigBean,
) {

	fun createTrackerMessage(
		i18nSource: I18nExceptionSource,
		context: CommandBaseContext? = null,
		args: Map<String, Any?> = emptyMap(),
	): MessageEmbed {
		val repository = VcsRepository.JWIZARD_CORE
		val deploymentVersion =
			vcsDeploymentSupplier.getDeploymentVersion(vcsConfig.getRepositoryName(repository))
		val (name, url) = vcsConfig.createSnapshotUrl(repository, deploymentVersion)

		val tracker = i18nSource.tracker
		val lang = context?.language

		val stringJoiner = StringJoiner("")
		stringJoiner.addKeyValue(
			I18nUtilSource.BUG_TRACKER,
			mdLink(tracker, createTrackerUrl(tracker)),
			lang
		)
		stringJoiner.add("\n")
		stringJoiner.addKeyValue(
			I18nUtilSource.COMPILATION_VERSION,
			if (url != null) mdLink(name, url) else name,
			lang
		)
		return MessageEmbedBuilder(i18n, jdaColorsCache, context)
			.setDescription(i18nSource, args)
			.appendDescription(stringJoiner.toString())
			.setColor(JdaColor.ERROR)
			.build()
	}

	fun createTrackerMessage(
		ex: CommandPipelineException,
	) = createTrackerMessage(ex.i18nExceptionSource, ex.commandBaseContext, ex.args)

	fun createTrackerLink(
		i18nSource: I18nExceptionSource,
		context: CommandBaseContext? = null,
	): ActionRow {
		val detailsMessage = i18n.t(I18nActionSource.DETAILS, context?.language)
		return ActionRow.of(Button.link(createTrackerUrl(i18nSource.tracker), detailsMessage))
	}

	fun createTrackerLink(
		ex: CommandPipelineException,
	) = createTrackerLink(ex.i18nExceptionSource, ex.commandBaseContext)

	private fun createTrackerUrl(tracker: Int): String {
		val baseUrl = environment.getProperty<String>(BotProperty.SERVICE_FRONT_URL)
		val urlReferTemplate = environment.getProperty<String>(BotProperty.LINK_FRAGMENT_ERROR_CODE)
		return urlReferTemplate.format(baseUrl, tracker)
	}

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
