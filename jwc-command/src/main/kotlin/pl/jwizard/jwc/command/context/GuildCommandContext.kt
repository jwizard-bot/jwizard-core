/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.context

import net.dv8tion.jda.api.Permission
import pl.jwizard.jwc.core.jda.command.GuildCommandBaseContext
import pl.jwizard.jwc.core.property.guild.GuildMultipleProperties
import pl.jwizard.jwc.core.property.guild.GuildProperty
import java.math.BigInteger

/**
 * Abstract class representing the context of a command execution within a guild. It provides access to command
 * arguments and guild-specific properties needed for command processing.
 *
 * @property guildCommandProperties Properties related to the guild where the command is executed.
 * @author Miłosz Gilga
 */
abstract class GuildCommandContext(
	private val guildCommandProperties: GuildMultipleProperties,
) : ArgumentContext(), GuildCommandBaseContext {

	override val language = getProp<String>(GuildProperty.LANGUAGE_TAG)
	override val guildDbId = getProp<BigInteger>(GuildProperty.DB_ID)
	override val suppressResponseNotifications = getProp<Boolean>(GuildProperty.SUPPRESS_RESPONSE_NOTIFICATIONS)

	/**
	 * The name of the DJ role in the guild, retrieved from the guild properties.
	 */
	val djRoleName = getProp<String>(GuildProperty.DJ_ROLE_NAME)

	/**
	 * The ID of the text channel used for music commands, if set in the guild properties.
	 */
	val musicTextChannelId = guildCommandProperties.getNullableProperty<String>(GuildProperty.MUSIC_TEXT_CHANNEL_ID)

	/**
	 * Checks if the command author has the specified permissions.
	 *
	 * @param permissions The list of permission strings to check against the author's permissions.
	 * @return True if the author has at least one of the specified permissions; otherwise, false.
	 */
	fun checkIfAuthorHasPermissions(vararg permissions: String) = permissions.any {
		author.hasPermission(Permission.valueOf(it))
	}

	/**
	 * Checks if the command author has any of the specified roles.
	 *
	 * @param roles The list of role names to check against the author's roles.
	 * @return True if the author has at least one of the specified roles; otherwise, false.
	 */
	fun checkIfAuthorHasRoles(vararg roles: String) = author.roles.any { roles.contains(it.name) }

	/**
	 * Retrieves a property value from the guild properties.
	 *
	 * @param property The [GuildProperty] to retrieve.
	 * @param T The expected type of the property value.
	 * @return The value of the property, cast to the specified type.
	 */
	private inline fun <reified T> getProp(property: GuildProperty) = guildCommandProperties.getProperty<T>(property)
}
