/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command

import java.math.BigInteger

/**
 * A data class that holds the command properties for a guild in the application. These properties include the guild's
 * database ID, language settings, command prefix, whether slash commands are enabled, and the name of the DJ role.
 *
 * @property guildDbId The unique database ID for the guild.
 * @property lang The language setting for the guild.
 * @property prefix The prefix used for text-based commands.
 * @property slashEnabled A flag indicating if slash commands are enabled for the guild.
 * @property djRoleName The name of the DJ role assigned in the guild.
 * @property musicTextChannelId The Discord ID of the music text channel. Possibly null.
 * @author Miłosz Gilga
 */
data class GuildCommandProperties(
	val guildDbId: BigInteger,
	val lang: String,
	val prefix: String,
	val slashEnabled: Boolean,
	val djRoleName: String,
	val musicTextChannelId: Long?,
)
