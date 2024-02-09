/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

import pl.jwizard.core.bot.BotInstance
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.UserException
import pl.jwizard.core.settings.GuildSettings
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel

object BotUtils {
	fun validateUserDetails(
		guildSettings: GuildSettings,
		event: CompoundCommandEvent,
	): ValidatedUserDetails {
		val guildDetails = guildSettings.getGuildProperties(event.guildId)
		return ValidatedUserDetails(
			isNotOwner = event.author.id != event.guild?.ownerId,
			isNotManager = event.member.hasPermission(Permission.MANAGE_SERVER),
			isNotDj = event.member.roles.none { it.name == guildDetails.djRoleName }
		)
	}

	fun checkIfMemberInGuildExist(event: CompoundCommandEvent, memberId: String): Member = event.guild?.members
		?.find { it.id == memberId }
		?: throw UserException.UserNotFoundInGuildException(event)

	fun getSystemTextChannel(guild: Guild): TextChannel = guild.systemChannel ?: guild.textChannels[0]

	fun getCompilationVersion() = BotInstance::class.java.`package`.implementationVersion ?: "DEVELOPMENT"
}
