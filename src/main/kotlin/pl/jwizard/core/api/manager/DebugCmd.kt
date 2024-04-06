/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.manager

import pl.jwizard.core.api.AbstractManagerCmd
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter
import pl.jwizard.core.util.SystemProperty
import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import net.dv8tion.jda.api.JDAInfo

@CommandListenerBean(id = BotCommand.DEBUG)
class DebugCmd(
	botConfiguration: BotConfiguration,
) : AbstractManagerCmd(
	botConfiguration
) {
	private val headerFromatter: (key: I18nLocale, guildId: String) -> String = { key, guild ->
		"\n**%s**\n".format(botConfiguration.i18nService.getMessage(key, guild).uppercase())
	}

	private val propertyFormatter: (key: I18nLocale, value: String, guildId: String) -> String =
		{ key, value, guildId ->
			"  `%s` :: %s".format(botConfiguration.i18nService.getMessage(key, guildId), value)
		}

	override fun executeManagerCmd(event: CompoundCommandEvent) {
		val guildDetails = botConfiguration.guildSettings.getGuildProperties(event.guildId)

		val totalMemoryHeap = Runtime.getRuntime().totalMemory() / 1024 / 1024
		val usedMemoryHeap = totalMemoryHeap - (Runtime.getRuntime().freeMemory() / 1024 / 1024)
		val guildId = event.guildId

		val systemProps = SystemProperty.getAllFormatted(botConfiguration, event.guildId)
		val propertiesData = mutableListOf(*systemProps.toTypedArray())

		propertiesData.add(headerFromatter(I18nMiscLocale.JVM_HEADER, guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.JVM_XMX_MEMORY, "$totalMemoryHeap MB", guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.JVM_USED_MEMORY, "$usedMemoryHeap MB", guildId))

		propertiesData.add(headerFromatter(I18nMiscLocale.GENERAL_HEADER, guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.BOT_LOCALE, guildDetails.locale, guildId))
		propertiesData.add(
			propertyFormatter(
				I18nMiscLocale.CURRENT_GUILD_OWNER_TAG,
				event.guild?.owner?.user?.asTag ?: "unknow",
				guildId
			)
		)
		propertiesData.add(propertyFormatter(I18nMiscLocale.CURRENT_GUILD_ID, guildId, guildId))

		propertiesData.add(headerFromatter(I18nMiscLocale.CONFIGURATION_HEADER, guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.DEFAULT_PREFIX, guildDetails.legacyPrefix, guildId))
		propertiesData.add(
			propertyFormatter(
				I18nMiscLocale.ENABLE_SLASH_COMMANDS,
				Formatter.boolStr(guildDetails.slashEnabled),
				guildId
			)
		)
		propertiesData.add(
			propertyFormatter(
				I18nMiscLocale.VOTE_MAX_WAITING_TIME,
				"${guildDetails.voting.timeToFinishSec} s",
				guildId
			)
		)
		propertiesData.add(
			propertyFormatter(
				I18nMiscLocale.LEAVE_CHANNEL_WAITING_TIME,
				"${guildDetails.inactivity.leaveEmptyChannelSec} s",
				guildId
			)
		)

		propertiesData.add(headerFromatter(I18nMiscLocale.VERSIONS_HEADER, guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.JDA_VERSION, JDAInfo.VERSION, guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.JDA_UTILITIES_VERSION, JDAUtilitiesInfo.VERSION, guildId))
		propertiesData.add(propertyFormatter(I18nMiscLocale.LAVAPLAYER_VERSION, PlayerLibrary.VERSION, guildId))

		val messageEmbed = CustomEmbedBuilder(event, botConfiguration)
			.addAuthor()
			.addDescription(placeholder = I18nResLocale.SYSTEM_DATA)
			.addColor(EmbedColor.WHITE)
			.build()

		val propertiesDataPaginator = createDefaultPaginator(propertiesData)
		event.appendEmbedMessage(messageEmbed) { propertiesDataPaginator.display(event.textChannel) }
	}
}
