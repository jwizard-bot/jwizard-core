/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api

import net.dv8tion.jda.api.entities.MessageEmbed
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.CommandModule
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.db.GuildDbProperty
import pl.jwizard.core.i18n.I18nLocale
import pl.jwizard.core.i18n.I18nMiscLocale
import pl.jwizard.core.util.DateUtils
import pl.jwizard.core.vote.GeneralVotingSystemHandler
import pl.jwizard.core.vote.VoteFinishData
import pl.jwizard.core.vote.VoteResponseData

abstract class AbstractVoteMusicCmd(
	botConfiguration: BotConfiguration,
	playerManager: PlayerManager
) : AbstractMusicCmd(
	botConfiguration,
	playerManager,
) {
	override fun executeMusicCmd(event: CompoundCommandEvent) {
		checkIfCommandModuleIsEnabled(event, CommandModule.VOTE)
		val responseData = executeVoteMusicCmd(event)
		val votingSystemHandler = GeneralVotingSystemHandler(
			response = responseData,
			event,
			botConfiguration
		)
		votingSystemHandler.initAndStart()
	}

	protected fun buildInitMessage(
		i18nPlaceholder: I18nLocale,
		params: Map<String, String>,
		event: CompoundCommandEvent,
	): MessageEmbed {
		val maxVotingTime = DateUtils.convertSecToMin(
			guildSettings.fetchDbProperty(
				GuildDbProperty.MAX_VOTING_TIME,
				event.guildId,
				Long::class
			)
		)
		return CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(i18nPlaceholder, params)
			.addFooter("${i18nService.getMessage(I18nMiscLocale.MAX_TIME_VOTING, event.lang)}: ${(maxVotingTime)}")
			.addColor(EmbedColor.WHITE)
			.build()
	}

	private fun buildEndVoteMessage(
		i18nPlaceholder: I18nLocale,
		description: String,
		data: VoteFinishData,
		embedColor: EmbedColor,
		event: CompoundCommandEvent,
	): MessageEmbed {
		val percentageRatio =
			"${guildSettings.fetchDbProperty(GuildDbProperty.VOTING_PERCENTAGE_RATIO, event.guildId, Int::class)}%"
		return CustomEmbedBuilder(botConfiguration, event)
			.addTitle(i18nService.getMessage(i18nPlaceholder, event.lang))
			.addDescription(description)
			.appendKeyValueField(I18nMiscLocale.VOTES_FOR_YES_NO_VOTING, "${data.forYes}/${data.forNo}")
			.appendKeyValueField(I18nMiscLocale.REQUIRED_TOTAL_VOTES_VOTING, "${data.required}/${data.total}")
			.appendKeyValueField(I18nMiscLocale.VOTES_RATIO_VOTING, percentageRatio)
			.addColor(embedColor)
			.build()
	}

	protected fun buildInitMessage(
		i18nPlaceholder: I18nLocale,
		event: CompoundCommandEvent,
	): MessageEmbed = buildInitMessage(i18nPlaceholder, emptyMap(), event)

	protected fun buildSuccessMessage(
		i18nPlaceholder: I18nLocale,
		params: Map<String, String>,
		data: VoteFinishData,
		event: CompoundCommandEvent,
	): MessageEmbed = buildEndVoteMessage(
		I18nMiscLocale.ON_SUCCESS_VOTING,
		description = i18nService.getMessage(i18nPlaceholder, params, event.lang),
		data, EmbedColor.WHITE, event,
	)

	protected fun buildSuccessMessage(
		i18nPlaceholder: I18nLocale,
		data: VoteFinishData,
		event: CompoundCommandEvent,
	): MessageEmbed = buildSuccessMessage(i18nPlaceholder, emptyMap(), data, event)

	protected fun buildFailureMessage(
		i18nPlaceholder: I18nLocale,
		params: Map<String, String>,
		data: VoteFinishData,
		event: CompoundCommandEvent,
	): MessageEmbed {
		val message = i18nService.getMessage(I18nMiscLocale.TOO_FEW_POSITIVE_VOTES_VOTING, event.lang) +
			". ${i18nService.getMessage(i18nPlaceholder, params, event.lang)}"
		return buildEndVoteMessage(I18nMiscLocale.ON_FAILURE_VOTING, message, data, EmbedColor.DOMINATE, event)
	}

	protected fun buildFailureMessage(
		i18nPlaceholder: I18nLocale,
		data: VoteFinishData,
		event: CompoundCommandEvent,
	): MessageEmbed = buildFailureMessage(i18nPlaceholder, emptyMap(), data, event)

	protected fun buildTimeoutMessage(
		i18nPlaceholder: I18nLocale,
		params: Map<String, String>,
		data: VoteFinishData,
		event: CompoundCommandEvent,
	): MessageEmbed = buildEndVoteMessage(
		I18nMiscLocale.ON_TIMEOUT_VOTING,
		description = i18nService.getMessage(i18nPlaceholder, params, event.lang),
		data, EmbedColor.TINT, event,
	)

	protected fun buildTimeoutMessage(
		i18nPlaceholder: I18nLocale,
		data: VoteFinishData,
		event: CompoundCommandEvent,
	): MessageEmbed = buildTimeoutMessage(i18nPlaceholder, emptyMap(), data, event)

	abstract fun executeVoteMusicCmd(event: CompoundCommandEvent): VoteResponseData
}
