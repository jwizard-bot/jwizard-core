package pl.jwizard.jwc.audio.gateway.discord

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel

interface GatewayVoiceStateInterceptor {
	// check if member is in audio channel
	fun inAudioChannel(member: Member): Boolean?

	// connect to specified audio channel in guild
	fun makeConnect(guild: Guild, channel: AudioChannel)

	// disconnect from audio channel
	fun disconnect(guild: Guild)
}
