package pl.jwizard.jwc.core.jda.embed

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorsCacheBean
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.keyValueFormat
import pl.jwizard.jwl.i18n.I18nBean
import pl.jwizard.jwl.i18n.I18nLocaleSource

class MessageEmbedBuilder(
	private val i18nBean: I18nBean,
	private val jdaColorsCache: JdaColorsCacheBean,
	private val context: CommandBaseContext? = null,
) : EmbedBuilder() {

	fun setTitle(title: String) = apply {
		super.setTitle(title)
	}

	fun setTitle(
		i18nLocaleSource: I18nLocaleSource,
		args: Map<String, Any?> = emptyMap()
	) = setTitle(i18nBean.t(i18nLocaleSource, context?.language, args))

	fun setDescription(
		i18nLocaleSource: I18nLocaleSource,
		args: Map<String, Any?> = emptyMap()
	) = apply {
		super.setDescription(i18nBean.t(i18nLocaleSource, context?.language, args))
	}

	fun setDescription(description: String) = apply {
		super.setDescription(description)
	}

	fun appendDescription(description: String) = apply {
		super.appendDescription("\n\n$description")
	}

	fun setColor(jdaColor: JdaColor) = apply {
		super.setColor(jdaColorsCache.getHexColor(jdaColor))
	}

	fun setFooter(i18nKey: I18nLocaleSource, value: Any, iconUrl: String? = null) = apply {
		super.setFooter(keyValueFormat(i18nBean.t(i18nKey, context?.language), value), iconUrl)
	}

	fun setArtwork(url: String?) = apply {
		url?.let { setThumbnail(url) }
	}

	fun setLocalArtwork(name: String?) = apply {
		name?.let { setThumbnail("attachment://$it") }
	}

	fun setValueField(value: Any?, inline: Boolean = true) = apply {
		addField("", value.toString(), inline)
	}

	fun setKeyValueField(
		key: I18nLocaleSource,
		value: Any?,
		inline: Boolean = true
	) = setKeyValueField(i18nBean.t(key, context?.language), value, inline)

	fun setKeyValueField(key: String, value: Any?, inline: Boolean = true) = apply {
		addField(MessageEmbed.Field("$key:", value.toString(), inline))
	}

	fun setSpace() = apply {
		addBlankField(true)
	}
}
