package pl.jwizard.jwc.core.audio

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor

interface DistributedAudioClient {
	val voiceDispatchInterceptor: VoiceDispatchInterceptor

	fun initClient()

	fun getPlayersCountInSelectedGuilds(guilds: List<Guild>): Int
}
