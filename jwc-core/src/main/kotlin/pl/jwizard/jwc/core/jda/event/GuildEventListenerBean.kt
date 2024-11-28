/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.event

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent
import net.dv8tion.jda.api.events.guild.GuildBanEvent
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
	 * Triggered when a guild is ready. This event is used to create guild settings and register slash commands for the
	 * guild.
	 *
	 * @param event The event triggered when the guild becomes ready.
	 */
	override fun onGuildReady(event: GuildReadyEvent) {
		val guild = event.guild
		val (arePersisted, errorMessage) = guildSettingsEventAction.createGuildSettings(guild.idLong, guild.locale.locale)
		if (errorMessage != null) {
			log.error("Unexpected exception while persisting guild: {}. Cause: {}.", guild.qualifier, errorMessage)
			guild.leave().queue { log.info("Leaved guild: {}.", guild.qualifier) }
			return
		}
		if (arePersisted) {
			log.info("Saved guild: {} settings into persisted storage.", guild.qualifier)
		}
		slashCommandRegisterer.registerGuildCommands(event.guild)
	}

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
