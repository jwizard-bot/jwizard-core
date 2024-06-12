/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.api.music

import org.apache.commons.validator.routines.UrlValidator
import pl.jwizard.core.api.AbstractMusicCmd
import pl.jwizard.core.audio.player.PlayerManagerFacade
import pl.jwizard.core.bot.BotConfiguration
import pl.jwizard.core.command.BotCommand
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.command.arg.CommandArgument
import pl.jwizard.core.command.reflect.CommandListenerBean

@CommandListenerBean(id = BotCommand.PLAY)
class PlayTrackCmd(
	botConfiguration: BotConfiguration,
	playerManagerFacade: PlayerManagerFacade
) : AbstractMusicCmd(
	botConfiguration,
	playerManagerFacade
) {
	init {
		onSameChannelWithBot = true
		selfJoinable = true
	}

	override fun executeMusicCmd(event: CompoundCommandEvent) {
		var searchPhrase = getArg<String>(CommandArgument.TRACK, event)

		val urlValidator = UrlValidator()
		val isUrlPattern = urlValidator.isValid(searchPhrase)
		searchPhrase = if (isUrlPattern) {
			searchPhrase.replace(" ", "")
		} else {
			"ytsearch: $searchPhrase audio"
		}
		val musicManager = playerManagerFacade.findMusicManager(event)
		musicManager.trackScheduler.setCompoundEvent(event)
		playerManagerFacade.loadAndPlay(event, searchPhrase, isUrlPattern)
	}
}
