/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.embed

import net.dv8tion.jda.api.EmbedBuilder
import pl.jwizard.jwc.core.i18n.I18nBean
import pl.jwizard.jwc.core.i18n.I18nLocaleSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.color.JdaColorStoreBean

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
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setTitle(title: String) = apply { super.setTitle(title) }

	/**
	 * Sets the author of the embed if context is available.
	 *
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setAuthor() = apply { context?.let { super.setAuthor(it.authorName, null, it.authorAvatarUrl) } }

	/**
	 * Sets the description of the embed using internationalization.
	 *
	 * @param i18nLocaleSource The source for localization.
	 * @param args Arguments to replace placeholders in the description.
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setDescription(i18nLocaleSource: I18nLocaleSource, args: Map<String, Any?> = emptyMap()) =
		apply { super.setDescription(i18nBean.t(i18nLocaleSource, context?.guildLanguage, args)) }

	/**
	 * Sets the description of the embed.
	 *
	 * @param description The description text to set.
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setDescription(description: String) = apply { super.setDescription(description) }

	/**
	 * Appends additional text to the current description.
	 *
	 * @param description The additional description text to append.
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun appendDescription(description: String) = apply { super.appendDescription("\n\n$description") }

	/**
	 * Sets the color of the embed based on the provided JDA color.
	 *
	 * @param jdaColor The color to apply to the embed.
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setColor(jdaColor: JdaColor) = apply { super.setColor(jdaColorStoreBean.getHexColor(jdaColor)) }

	/**
	 * Sets the footer of the embed with optional icon.
	 *
	 * @param text The footer text to display.
	 * @param iconUrl Optional URL of the icon to display in the footer.
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setFooter(text: String, iconUrl: String) = apply { super.setFooter(text, iconUrl) }

	/**
	 * Sets a thumbnail image for the embed.
	 *
	 * @param thumbnailUrl The URL of the thumbnail image.
	 * @return The current instance of MessageEmbedBuilder for method chaining.
	 */
	fun setThumbnail(thumbnailUrl: String) = apply { super.setThumbnail(thumbnailUrl) }
}
