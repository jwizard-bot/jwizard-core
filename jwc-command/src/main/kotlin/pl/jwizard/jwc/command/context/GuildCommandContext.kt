package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.Permission
import pl.jwizard.jwc.core.jda.command.GuildCommandBaseContext
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import java.math.BigInteger

abstract class GuildCommandContext(
	guildCommandProperties: GuildMultipleProperties,
) : ArgumentContext(), GuildCommandBaseContext {

	val djRoleName = guildCommandProperties.getProperty<String>(GuildProperty.DJ_ROLE_NAME)

	val musicTextChannelId = guildCommandProperties
		.getNullableProperty<String>(GuildProperty.MUSIC_TEXT_CHANNEL_ID)

	override val language = guildCommandProperties
		.getProperty<String>(GuildProperty.LANGUAGE_TAG)

	override val guildDbId = guildCommandProperties.getProperty<BigInteger>(GuildProperty.DB_ID)

	override val suppressResponseNotifications = guildCommandProperties
		.getProperty<Boolean>(GuildProperty.SUPPRESS_RESPONSE_NOTIFICATIONS)

	fun checkIfAuthorHasPermissions(vararg permissions: String) = permissions.any {
		author.hasPermission(Permission.valueOf(it))
	}

	fun checkIfAuthorHasRoles(vararg roles: String) = author.roles.any { roles.contains(it.name) }
}
