/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.embed

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.apache.commons.lang3.StringUtils
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.AbstractBotException
import pl.jwizard.core.exception.I18nExceptionLocale
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nResLocale
import java.util.*

class CustomEmbedBuilder(
	private val event: CompoundCommandEvent?,
	private val botConfiguration: BotConfiguration,
	private val lang: String,
) : EmbedBuilder() {

	private val i18nService = botConfiguration.i18nService

	constructor(
		botConfiguration: BotConfiguration,
		lang: String
	) : this(null, botConfiguration, lang)

	constructor(
		botConfiguration: BotConfiguration,
		event: CompoundCommandEvent
	) : this(event, botConfiguration, event.lang)

	fun addTitle(content: String): CustomEmbedBuilder {
		setTitle(content)
		return this
	}

	fun addAuthor(): CustomEmbedBuilder {
		setAuthor(event?.authorTag, null, event?.authorAvatarUrl)
		return this
	}

	fun addAuthor(user: User): CustomEmbedBuilder {
		setAuthor(user.name, null, user.avatarUrl ?: user.defaultAvatarUrl)
		return this
	}

	fun addAuthor(name: String?, avatarUri: String?): CustomEmbedBuilder {
		setAuthor(name, null, avatarUri)
		return this
	}

	fun addDescription(placeholder: I18nLocale, params: Map<String, Any>): CustomEmbedBuilder {
		setDescription(i18nService.getMessage(placeholder, params, getMessLang()))
		return this
	}

	fun addDescription(placeholder: I18nLocale): CustomEmbedBuilder = addDescription(placeholder, emptyMap())

	fun addDescription(placeholder: String): CustomEmbedBuilder {
		setDescription(placeholder)
		return this
	}

	fun appendDescriptionList(elements: Map<I18nLocale, Map<String, Any>>): CustomEmbedBuilder {
		val stringJoiner = StringJoiner(StringUtils.EMPTY)
		var i = 0
		stringJoiner.add("\n\n")
		for ((i18nLocale, params) in elements) {
			stringJoiner.add("* ${i18nService.getMessage(i18nLocale, params, getMessLang())}")
			if (i++ < elements.size) {
				stringJoiner.add("\n")
			}
		}
		appendDescription(stringJoiner.toString())
		return this
	}

	fun appendKeyValueField(key: I18nLocale, value: Any): CustomEmbedBuilder {
		addField(
			MessageEmbed.Field(
				"${i18nService.getMessage(key, getMessLang())}:",
				value.toString(),
				true
			)
		)
		return this
	}

	fun appendField(key: String, value: Any, inline: Boolean): CustomEmbedBuilder {
		addField(key, value.toString(), inline)
		return this
	}

	fun appendValueField(value: Any, inline: Boolean): CustomEmbedBuilder {
		addField("", value.toString(), inline)
		return this
	}

	fun addSpace(): CustomEmbedBuilder {
		addBlankField(true)
		return this
	}

	fun addColor(embedColor: EmbedColor): CustomEmbedBuilder {
		setColor(embedColor.color())
		return this
	}

	fun addThumbnail(url: String?): CustomEmbedBuilder {
		setThumbnail(url)
		return this
	}

	fun addFooter(content: String): CustomEmbedBuilder {
		setFooter(content)
		return this
	}

	fun buildErrorMessage(
		placeholder: I18nExceptionLocale,
		params: Map<String, Any>,
	): MessageEmbed = this
		.setAuthor(event?.authorTag, null, event?.authorAvatarUrl)
		.setDescription(i18nService.getMessage(placeholder, params, getMessLang()))
		.appendDescription("\n\n${placeholder.createBugTrackerMessage(botConfiguration, getMessLang())}")
		.setColor(EmbedColor.ERROR.color())
		.build()

	fun buildErrorMessage(placeholder: I18nExceptionLocale): MessageEmbed = buildErrorMessage(placeholder, emptyMap())

	fun buildErrorMessage(ex: AbstractBotException): MessageEmbed = buildErrorMessage(ex.i18nLocale, ex.variables)

	fun buildBaseMessage(
		placeholder: I18nLocale,
		params: Map<String, Any>,
	): MessageEmbed = this
		.setDescription(i18nService.getMessage(placeholder, params, getMessLang()))
		.setColor(EmbedColor.WHITE.color())
		.build()

	fun buildBaseMessage(placeholder: I18nLocale): MessageEmbed = buildBaseMessage(placeholder, emptyMap())

	fun buildTrackMessage(
		placeholder: I18nResLocale,
		params: Map<String, Any>,
		thumbnailUrl: String?,
	) = this
		.addDescription(placeholder, params)
		.addColor(EmbedColor.WHITE)
		.addThumbnail(thumbnailUrl)
		.build()

	private fun getMessLang(): String = event?.lang ?: lang

}
