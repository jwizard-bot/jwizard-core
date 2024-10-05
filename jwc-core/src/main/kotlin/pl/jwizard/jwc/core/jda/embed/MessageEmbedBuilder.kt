/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.embed

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean
import pl.jwizard.jwc.core.util.keyValueFormat

/**
 * A builder class for creating Discord message embeds with additional customization options.
 *
 * @property context Optional context for the message, containing information like author details.
 * @property i18nBean Internationalization bean for translating messages.
 * @property jdaColorStoreBean Bean for accessing color configurations.
 * @author Miłosz Gilga
 */
class MessageEmbedBuilder(
	private val i18nBean: I18nBean,
	private val jdaColorStoreBean: JdaColorStoreBean,
	private val context: MessageBaseContext? = null,
) : EmbedBuilder() {

	/**
	 * Sets the title of the embed.
	 *
	 * @param title The title text to set.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setTitle(title: String) = apply { super.setTitle(title) }

	/**
	 * Sets the author of the embed if context is available.
	 *
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setAuthor() = apply { context?.let { super.setAuthor(it.authorName, null, it.authorAvatarUrl) } }

	/**
	 * Sets the description of the embed using internationalization.
	 *
	 * @param i18nLocaleSource The source for localization.
	 * @param args Arguments to replace placeholders in the description.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setDescription(i18nLocaleSource: I18nLocaleSource, args: Map<String, Any?> = emptyMap()) = apply {
		super.setDescription(i18nBean.t(i18nLocaleSource, context?.guildLanguage, args))
	}

	/**
	 * Sets the description of the embed.
	 *
	 * @param description The description text to set.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setDescription(description: String) = apply { super.setDescription(description) }

	/**
	 * Appends additional text to the current description.
	 *
	 * @param description The additional description text to append.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun appendDescription(description: String) = apply { super.appendDescription("\n\n$description") }

	/**
	 * Sets the color of the embed based on the provided JDA color.
	 *
	 * @param jdaColor The color to apply to the embed.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setColor(jdaColor: JdaColor) = apply { super.setColor(jdaColorStoreBean.getHexColor(jdaColor)) }

	/**
	 * Sets the footer of the embed using internationalization.
	 *
	 * @param i18nKey The key for the localization of the footer text.
	 * @param value The value to display in the footer.
	 * @param iconUrl Optional URL of an icon to display next to the footer text.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setFooter(i18nKey: I18nLocaleSource, value: Any, iconUrl: String? = null) = apply {
		super.setFooter(keyValueFormat(i18nBean.t(i18nKey, context?.guildLanguage), value), iconUrl)
	}

	/**
	 * Sets an artwork image for the embed.
	 *
	 * @param url The URL of the artwork image. If url is `null`, image cannot be rendered.
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setArtwork(url: String?) = apply { url?.let { super.setThumbnail(url) } }

	/**
	 * Sets a value field in the embed with an optional inline display.
	 *
	 * @param value The value to display in the field.
	 * @param inline Whether to display the field inline with others (default is true).
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setValueField(value: Any, inline: Boolean = true) = apply { addField("", value.toString(), inline) }

	/**
	 * Sets a key-value field in the embed with a localized key.
	 *
	 * @param key The key for the localization of the field name.
	 * @param value The value to display in the field.
	 * @param inline Whether to display the field inline with others (default is true).
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setKeyValueField(key: I18nLocaleSource, value: Any, inline: Boolean = true) = apply {
		addField(MessageEmbed.Field("$key:", value.toString(), inline))
	}

	/**
	 * Adds a blank field to the embed for spacing purposes. This method can be used to create visual separation between
	 * fields.
	 *
	 * @return The current instance of [MessageEmbedBuilder] for method chaining.
	 */
	fun setSpace() = apply { addBlankField(true) }
}
