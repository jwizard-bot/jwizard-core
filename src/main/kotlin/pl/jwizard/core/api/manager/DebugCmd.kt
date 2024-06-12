/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.manager

import com.jagrosh.jdautilities.commons.JDAUtilitiesInfo
import com.sedmelluq.discord.lavaplayer.tools.PlayerLibrary
import net.dv8tion.jda.api.JDAInfo
import org.apache.commons.io.FileUtils
import pl.jwizard.core.api.AbstractManagerCmd
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.bot.properties.BotProperties
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.exception.UtilException
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.BotUtils
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.util.Formatter
import pl.jwizard.core.util.SystemProperty

@CommandListenerBean(id = BotCommand.DEBUG)
class DebugCmd(
	botConfiguration: BotConfiguration,
	private val botProperties: BotProperties,
) : AbstractManagerCmd(
	botConfiguration
) {
	private val headerFromatter: (key: I18nLocale, event: CompoundCommandEvent) -> String = { key, event ->
		"\n**%s**\n".format(botConfiguration.i18nService.getMessage(key, event.lang).uppercase())
	}

	private val propertyFormatter: (key: I18nLocale, value: String, event: CompoundCommandEvent) -> String =
		{ key, value, event ->
			"  `%s` :: %s".format(botConfiguration.i18nService.getMessage(key, event.lang), value)
		}

	override fun executeManagerCmd(event: CompoundCommandEvent) {
		val guildDetails = botConfiguration.guildSettingsSupplier.fetchGuildCombinedProperties(event.guildId)
			?: throw UtilException.UnexpectedException("Couldn't fetch guild debug details")

		val (buildVersion, buildDate) = botProperties.deployment
		val totalMemoryHeap = Runtime.getRuntime().totalMemory()
		val usedMemoryHeap = totalMemoryHeap - Runtime.getRuntime().freeMemory()
		val guildId = event.guildId

		val systemProps = SystemProperty.getAllFormatted(botConfiguration, event.lang)
		val propertiesData = mutableListOf(*systemProps.toTypedArray())

		propertiesData.add(headerFromatter(I18nMiscLocale.JVM_HEADER, event))
		propertiesData.add(formatBytesMessage(I18nMiscLocale.JVM_XMX_MEMORY, totalMemoryHeap, event))
		propertiesData.add(formatBytesMessage(I18nMiscLocale.JVM_USED_MEMORY, usedMemoryHeap, event))

		propertiesData.add(headerFromatter(I18nMiscLocale.GENERAL_HEADER, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.COMPILATION_VERSION, buildVersion, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.DEPLOYMENT_DATE, buildDate, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.BOT_LOCALE, event.lang, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.CURRENT_GUILD_OWNER_TAG, BotUtils.getOwnerTag(event), event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.CURRENT_GUILD_ID, guildId, event))

		propertiesData.add(headerFromatter(I18nMiscLocale.VERSIONS_HEADER, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.JDA_VERSION, JDAInfo.VERSION, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.JDA_UTILITIES_VERSION, JDAUtilitiesInfo.VERSION, event))
		propertiesData.add(propertyFormatter(I18nMiscLocale.LAVAPLAYER_VERSION, PlayerLibrary.VERSION, event))

		val (
			votingPercentageRatio,
			timeToFinishVotingSec,
			musicTextChannelId,
			maxRepeatsOfTrack,
			leaveEmptyChannelSec,
			leaveNoTracksSec,
			defaultVolume,
			randomAutoChooseSec,
			timeAfterAutoChooseSec,
			maxTracksToChoose,
		) = guildDetails

		val propertyMap = mapOf<I18nLocale, String>(
			I18nMiscLocale.DEFAULT_PREFIX to event.legacyPrefix,
			I18nMiscLocale.ENABLE_SLASH_COMMANDS to Formatter.boolStr(event.isSlashEnabled),
			I18nMiscLocale.MUSIC_TEXT_CHANNEL to BotUtils.getChannelTagName(event.guild, musicTextChannelId),
			I18nMiscLocale.DJ_ROLE_NAME to event.djRoleName,
			I18nMiscLocale.VOTING_PERCENTAGE_RATIO to "$votingPercentageRatio%",
			I18nMiscLocale.VOTE_MAX_WAITING_TIME to DateUtils.convertSecToMin(timeToFinishVotingSec),
			I18nMiscLocale.MAX_REPEATS_OF_TRACK to maxRepeatsOfTrack.toString(),
			I18nMiscLocale.LEAVE_EMPTY_CHANNEL_SEC to DateUtils.convertSecToMin(leaveEmptyChannelSec),
			I18nMiscLocale.LEAVE_NO_TRACKS_SEC to DateUtils.convertSecToMin(leaveNoTracksSec),
			I18nMiscLocale.DEFAULT_VOLUME to defaultVolume.toString(),
			I18nMiscLocale.RANDOM_AUTO_CHOOSE_TRACK to Formatter.boolStr(randomAutoChooseSec),
			I18nMiscLocale.TIME_AFTER_AUTO_CHOOSE_SEC to DateUtils.convertSecToMin(timeAfterAutoChooseSec),
			I18nMiscLocale.MAX_TRACKS_TO_CHOOSE to maxTracksToChoose.toString(),
		)
		propertiesData.add(headerFromatter(I18nMiscLocale.CONFIGURATION_HEADER, event))
		propertyMap.forEach { (key, value) -> propertiesData.add(propertyFormatter(key, value, event)) }

		val messageEmbed = CustomEmbedBuilder(botConfiguration, event.lang)
			.addAuthor()
			.addDescription(placeholder = I18nResLocale.SYSTEM_DATA)
			.addColor(EmbedColor.WHITE)
			.build()

		val propertiesDataPaginator = createDefaultPaginator(propertiesData)
		event.appendEmbedMessage(messageEmbed) { propertiesDataPaginator.display(event.textChannel) }
	}

	private fun formatBytesMessage(locale: I18nLocale, bytes: Long, event: CompoundCommandEvent): String {
		return propertyFormatter(locale, FileUtils.byteCountToDisplaySize(bytes), event)
	}
}
