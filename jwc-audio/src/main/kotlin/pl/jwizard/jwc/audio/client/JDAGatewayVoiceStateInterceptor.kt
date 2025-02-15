package pl.jwizard.jwc.audio.client

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import pl.jwizard.jwc.audio.gateway.discord.GatewayVoiceStateInterceptor
import pl.jwizard.jwc.core.jda.JdaShardManagerBean
import pl.jwizard.jwl.ioc.stereotype.SingletonComponent

@SingletonComponent
class JDAGatewayVoiceStateInterceptor(
	private val jdaShardManager: JdaShardManagerBean,
) : GatewayVoiceStateInterceptor {
	override fun disconnect(guild: Guild) {
		jdaShardManager.getDirectAudioController(guild)?.disconnect(guild)
	}

	override fun inAudioChannel(member: Member) = member.voiceState?.inAudioChannel()

	override fun makeConnect(guild: Guild, channel: AudioChannel) {
		jdaShardManager.getDirectAudioController(guild)?.connect(channel)
	}
}
