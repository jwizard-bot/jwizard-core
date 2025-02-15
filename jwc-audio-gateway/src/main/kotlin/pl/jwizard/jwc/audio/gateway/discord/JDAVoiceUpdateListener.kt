package pl.jwizard.jwc.audio.gateway.discord

import dev.arbjerg.lavalink.protocol.v4.VoiceState
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor.VoiceServerUpdate
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor.VoiceStateUpdate
import pl.jwizard.jwc.audio.gateway.AudioClient
import pl.jwizard.jwc.audio.gateway.link.LinkState

class JDAVoiceUpdateListener(private val audioClient: AudioClient) : VoiceDispatchInterceptor {
	// invoked when bot connect to audio channel (from definition, any audio channel can have
	// different gateway endpoint)
	// MUST BE invoked before transfer to new node, because we have gateway endpoint information
	// only from this method
	override fun onVoiceServerUpdate(update: VoiceServerUpdate) {
		val state = VoiceState(
			update.token,
			update.endpoint,
			update.sessionId
		)
		val link = audioClient.getOrCreateLink(update.guildIdLong)
		audioClient.voiceGatewayUpdateTrigger?.complete(null)
		link.updateNodeVoiceState(state)
	}

	// invoked when bot changed his audio state (join/leave audio channel)
	override fun onVoiceStateUpdate(update: VoiceStateUpdate): Boolean {
		val channel = update.channel
		// get cached link and player, otherwise ignored change
		val link = audioClient.getLinkIfCached(update.guildIdLong) ?: return false
		val player = link.selectedNode.getCachedPlayer(update.guildIdLong) ?: return false
		val playerState = player.state

		if (channel == null) {
			// when bot leaves the channel and player is disconnected, set link to DISCONNECTED and
			// remove audio player from audio server, otherwise set link state to CONNECTED
			val updatedLinkState = if (playerState.connected) {
				LinkState.CONNECTED
			} else {
				link.destroy().subscribe()
				LinkState.DISCONNECTED
			}
			link.updateState(updatedLinkState)
		}
		return playerState.connected
	}
}
