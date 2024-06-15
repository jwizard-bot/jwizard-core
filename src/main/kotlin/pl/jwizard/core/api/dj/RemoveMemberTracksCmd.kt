/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.dj

import pl.jwizard.core.api.AbstractDjCmd
import pl.jwizard.core.audio.player.PlayerManager
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.embed.CustomEmbedBuilder
import pl.jwizard.core.command.embed.EmbedColor
import pl.jwizard.core.command.reflect.CommandListenerBean
import pl.jwizard.core.i18n.I18nResLocale
import pl.jwizard.core.util.Formatter

@CommandListenerBean(id = BotCommand.TRACKSRM)
class RemoveMemberTracksCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManager
) : AbstractDjCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
	}

	override fun executeDjCmd(event: CompoundCommandEvent) {
		val userId = getArg<String>(CommandArgument.MEMBER, event)

		val removedTrackInfo = playerManager.removeTracksFromMember(event, userId)
		val pageableRemovedTracks = removedTrackInfo.removedTracks
			.mapIndexed { index, track -> Formatter.createRichPageableTrackInfo(index, track) }

		val removedTracksListPaginator = createDefaultPaginator(pageableRemovedTracks)
		val embedMessage = CustomEmbedBuilder(botConfiguration, event)
			.addAuthor()
			.addDescription(
				placeholder = I18nResLocale.REMOVED_TRACKS_FROM_SELECTED_MEMBER,
				params = mapOf(
					"countOfRemovedTracks" to pageableRemovedTracks.size,
					"memberTag" to removedTrackInfo.member.user.asTag,
				)
			)
			.addColor(EmbedColor.WHITE)
			.build()
		event.appendEmbedMessage(embedMessage) { removedTracksListPaginator.display(event.textChannel) }
	}
}
