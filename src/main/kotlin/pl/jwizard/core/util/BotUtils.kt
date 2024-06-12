/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.TextChannel
import pl.jwizard.core.command.CompoundCommandEvent
import pl.jwizard.core.exception.UserException

object BotUtils {
	fun validateUserDetails(event: CompoundCommandEvent): ValidatedUserDetails = ValidatedUserDetails(
		isNotOwner = event.author.id != event.guild?.ownerId,
		isNotManager = event.member.hasPermission(Permission.MANAGE_SERVER),
		isNotDj = event.member.roles.none { it.name == event.djRoleName }
	)

	fun checkIfMemberInGuildExist(event: CompoundCommandEvent, memberId: String): Member = event.guild?.members
		?.find { it.id == memberId }
		?: throw UserException.UserNotFoundInGuildException(event)

	fun getSystemTextChannel(guild: Guild): TextChannel = guild.systemChannel ?: guild.textChannels[0]

	fun getLang(lang: String, languagesMap: Map<String, String?>): String =
		languagesMap[lang] ?: languagesMap.entries.first().value ?: "NULL"

	fun getOwnerTag(event: CompoundCommandEvent): String = event.guild?.owner?.user?.asTag ?: "unknow"

	fun getChannelTagName(guild: Guild?, channelId: String?): String {
		val nullReplacement = "NULL"
		if (channelId == null || guild == null) {
			return nullReplacement
		}
		return guild.textChannels.find { it.id == channelId }
			?.name ?: nullReplacement
	}
}
