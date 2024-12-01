/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GuildBanEvent
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.events.guild.GuildReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pl.jwizard.jwc.core.jda.spi.GuildSettingsEventAction
import pl.jwizard.jwc.core.jda.spi.SlashCommandRegisterer
import pl.jwizard.jwc.core.property.EnvironmentBean
import pl.jwizard.jwc.core.property.guild.GuildProperty
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwl.util.logger

/**
 * Listener responsible for handling various guild-related events, such as guild initialization, leaving, banning
 * members, and deleting channels.
 *
 * @property guildSettingsEventAction Action handler for managing guild settings.
 * @property slashCommandRegisterer Responsible for registering slash commands within the guild.
 * @property environmentBean Provides access to environment-specific properties for the guild.
 * @author Miłosz Gilga
 */
@JdaEventListenerBean
class GuildEventListenerBean(
	private val guildSettingsEventAction: GuildSettingsEventAction,
	private val slashCommandRegisterer: SlashCommandRegisterer,
	private val environmentBean: EnvironmentBean,
) : ListenerAdapter() {

	companion object {
		private val log = logger<GuildEventListenerBean>()
	}

	/**
	 * Triggered when the bot joins a new guild. This method initializes the settings for the guild and registers its
	 * slash commands.
	 *
	 * @param event The event triggered when the bot joins a guild.
	 */
	override fun onGuildJoin(event: GuildJoinEvent) = persistGuildSettings(event.guild)

	/**
	 * Triggered when a guild is ready. This event is used to create guild settings and register slash commands for the
	 * guild.
	 *
	 * @param event The event triggered when the guild becomes ready.
	 */
	override fun onGuildReady(event: GuildReadyEvent) = persistGuildSettings(event.guild)

	/**
	 * Triggered when a guild is left. This method handles cleanup of guild settings upon the guild's exit.
	 *
	 * @param event The event triggered when the bot leaves the guild.
	 */
	override fun onGuildLeave(event: GuildLeaveEvent) = deleteGuildSettings(event.guild)

	/**
	 * Triggered when a member is banned from the guild. This method also handles cleanup of guild settings.
	 *
	 * @param event The event triggered when a member is banned from the guild.
	 */
	override fun onGuildBan(event: GuildBanEvent) = deleteGuildSettings(event.guild)

	/**
	 * Triggered when a text channel is deleted. If the deleted channel is the music text channel, it's ID is removed
	 * from the guild's settings.
	 *
	 * @param event The event triggered when a channel is deleted in the guild.
	 */
	override fun onChannelDelete(event: ChannelDeleteEvent) {
		val channel = event.channel
		if (channel.type != ChannelType.TEXT) {
			return
		}
		val guild = event.guild
		val musicTextChannelId = environmentBean.getGuildProperty<String>(GuildProperty.MUSIC_TEXT_CHANNEL_ID, guild.idLong)
		if (musicTextChannelId == channel.id) {
			guildSettingsEventAction.deleteDefaultMusicTextChannel(guild.idLong)
		}
	}

	/**
	 * Saves the settings for a guild in persistent storage and registers the slash commands specific to the guild. If
	 * saving the settings fails, the bot leaves the guild.
	 *
	 * @param guild The guild for which settings are being persisted.
	 */
	private fun persistGuildSettings(guild: Guild) {
		val (areNewlyPersisted, errorMessage) = guildSettingsEventAction.createGuildSettings(
			guild.idLong,
			guild.locale.locale,
		)
		if (errorMessage != null) {
			log.error("Unexpected exception while persisting guild: {}. Cause: {}.", guild.qualifier, errorMessage)
			guild.leave().queue { log.info("Leaved guild: {}.", guild.qualifier) }
			return
		}
		if (areNewlyPersisted) {
			log.debug("Saved guild: {} settings into persisted storage.", guild.qualifier)
		}
		slashCommandRegisterer.registerGuildCommands(guild)
	}

	/**
	 * Deletes guild settings from the persistent storage when the guild is left or a member is banned.
	 *
	 * @param guild The guild whose settings are to be deleted.
	 */
	private fun deleteGuildSettings(guild: Guild) {
		val rowsAffected = guildSettingsEventAction.deleteGuildSettings(guild.idLong)
		if (rowsAffected > 0) {
			log.info("Delete guild: {} settings from persisted storage. Rows affected: {}.", guild.qualifier, rowsAffected)
		}
	}
}
