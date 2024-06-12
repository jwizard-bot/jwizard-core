/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.db

import net.dv8tion.jda.api.entities.Guild
import kotlin.reflect.KClass

interface GuildSettingsSupplier {
	fun persistGuildSettings(guild: Guild)
	fun removeDefaultMusicTextChannel(guild: Guild)
	fun deleteGuildSettings(guild: Guild)
	fun <T : Any> fetchDbProperty(property: GuildDbProperty, guildId: String, clazz: KClass<T>): T
	fun fetchGuildCommandProperties(guildId: String): GuildCommandPropertiesDto?
	fun checkIfCommandIsEnabled(guildId: Long, commandId: Long, isSlashCommand: Boolean): Boolean
	fun fetchVotingSongChooserSettings(guildId: Long): VotingSongChooserSettings
	fun fetchGuildLang(guildId: String): String
	fun fetchGuildCombinedProperties(guildId: String): GuildCombinedPropertiesDto?
}