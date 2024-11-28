/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.client

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import pl.jwizard.jwac.gateway.GatewayVoiceStateInterceptor
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

/**
 * Implementation of [GatewayVoiceStateInterceptor] for managing voice state interactions with JDA (Java Discord API).
 *
 * This component provides methods to manage audio connections, check member states in audio channels, and handle
 * audio-related events in a Discord guild. It integrates with the [JdaShardManagerBean] for controlling audio
 * operations.
 *
 * @property jdaShardManager The shard manager bean for managing audio connections in Discord guilds.
 * @author Miłosz Gilga
 */
@SingletonComponent
class JDAGatewayVoiceStateInterceptor(
	private val jdaShardManager: JdaShardManagerBean,
) : GatewayVoiceStateInterceptor {

	/**
	 * Disconnects the bot from the voice channel in the specified guild.
	 *
	 * This method retrieves the audio controller for the provided guild and issues a disconnect command to remove the
	 * bot from the current voice channel.
	 *
	 * @param guild The Discord guild where the bot should be disconnected.
	 */
	override fun disconnect(guild: Guild) {
		jdaShardManager.getDirectAudioController(guild)?.disconnect(guild)
	}

	/**
	 * Checks if the specified member is currently in a voice channel.
	 *
	 * This method evaluates the voice state of the member to determine if they are present in any audio channel.
	 *
	 * @param member The Discord member whose voice state is to be checked.
	 * @return `true` if the member is in a voice channel, `false` otherwise.
	 */
	override fun inAudioChannel(member: Member) = member.voiceState?.inAudioChannel()

	/**
	 * Connects the bot to the specified audio channel in a guild.
	 *
	 * This method retrieves the audio controller for the guild and issues a connect command to join the specified audio
	 * channel.
	 *
	 * @param guild The Discord guild where the connection should be made.
	 * @param channel The audio channel the bot should connect to.
	 */
	override fun makeConnect(guild: Guild, channel: AudioChannel) {
		jdaShardManager.getDirectAudioController(guild)?.connect(channel)
	}
}
