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

@JdaEventListenerBean
class GuildEventListenerBean(
	private val guildSettingsEventAction: GuildSettingsEventAction,
	private val slashCommandRegisterer: SlashCommandRegisterer,
	private val environmentBean: EnvironmentBean,
) : ListenerAdapter() {

	companion object {
		private val log = logger<GuildEventListenerBean>()
	}

	override fun onGuildJoin(event: GuildJoinEvent) = persistGuildSettings(event.guild)

	override fun onGuildReady(event: GuildReadyEvent) = persistGuildSettings(event.guild)

	override fun onGuildLeave(event: GuildLeaveEvent) = deleteGuildSettings(event.guild)

	override fun onGuildBan(event: GuildBanEvent) = deleteGuildSettings(event.guild)

	override fun onChannelDelete(event: ChannelDeleteEvent) {
		val channel = event.channel
		if (channel.type != ChannelType.TEXT) {
			return
		}
		val guild = event.guild
		val musicTextChannelId = environmentBean
			.getGuildProperty<String>(GuildProperty.MUSIC_TEXT_CHANNEL_ID, guild.idLong)
		if (musicTextChannelId == channel.id) {
			guildSettingsEventAction.deleteDefaultMusicTextChannel(guild.idLong)
		}
	}

	private fun persistGuildSettings(guild: Guild) {
		val (areNewlyPersisted, errorMessage) = guildSettingsEventAction.createGuildSettings(
			guild.idLong,
			guild.locale.locale,
		)
		if (errorMessage != null) {
			log.error(
				"Unexpected exception while persisting guild: {}. Cause: {}.",
				guild.qualifier,
				errorMessage
			)
			guild.leave().queue { log.info("Leaved guild: {}.", guild.qualifier) }
			return
		}
		if (areNewlyPersisted) {
			log.debug("Saved guild: {} settings into persisted storage.", guild.qualifier)
		}
		slashCommandRegisterer.registerGuildCommands(guild)
	}

	private fun deleteGuildSettings(guild: Guild) {
		val rowsAffected = guildSettingsEventAction.deleteGuildSettings(guild.idLong)
		if (rowsAffected > 0) {
			log.info(
				"Delete guild: {} settings from persisted storage. Rows affected: {}.",
				guild.qualifier,
				rowsAffected
			)
		}
	}
}
