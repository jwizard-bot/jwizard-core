/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.http

import pl.jwizard.core.bot.BotProperties

enum class ApiUrl(
	private val url: String
) {
	STANDALONE_LOGIN("v1/identity/standalone/login"),
	STANDALONE_REFRESH("v1/identity/standalone/refresh"),
	ALL_COMMANDS_WITH_CATEGORIES("v1/command/all"),
	STANDALONE_GUILD_SETTINGS("v1/guild/standalone/settings"),
	REMOVE_MUSIC_TEXT_CHANNEL("v1/guild/standalone/settings/music-channel/guild"),
	;

	fun getUrl(botProperties: BotProperties): String = "${botProperties.apiHost}/api/${url}"
}
