/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.util

import net.dv8tion.jda.api.entities.Guild

/**
 * Utility singleton for formatting and converting various data types to string representations. This object provides
 * methods for formatting data related to Discord guilds.
 *
 * @author Miłosz Gilga
 */
object Formatter {

	/**
	 * Formats the provided [Guild] object into a string representation.
	 *
	 * @param guild The [Guild] object to format. This should not be `null`.
	 * @return A formatted string that includes the guild's name and ID.
	 *         Example format: `"GuildName <@GuildID>"`.
	 */
	fun guildQualifier(guild: Guild) = "\"${guild.name} <@${guild.id}>\""
}
