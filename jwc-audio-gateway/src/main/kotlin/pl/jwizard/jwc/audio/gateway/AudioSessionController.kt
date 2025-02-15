package pl.jwizard.jwc.audio.gateway

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import pl.jwizard.jwc.audio.gateway.discord.GatewayVoiceStateInterceptor
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.node.NodePool
import pl.jwizard.jwl.util.logger
import java.util.concurrent.CompletableFuture

class AudioSessionController(
	private val audioClient: AudioClient,
	private val gatewayVoiceStateInterceptor: GatewayVoiceStateInterceptor,
) {
	companion object {
		private val log = logger<AudioSessionController>()
	}

	// what is node transfer? node transfer is switching current node in link from one pool to
	// another, for example. from QUEUE to CONTINUOUS pool
	// after successfully transfer triggers onTransferNode callback
	fun loadAndTransferToNode(
		guild: Guild,
		pool: NodePool,
		authorMember: Member,
		selfMember: Member,
		onTransferNode: (AudioNode) -> Unit,
	): Boolean {
		val guildId = guild.idLong
		audioClient.updateGuildNodePool(guildId, pool)
		log.debug("Switch to: {} pool in guild: {}.", guildId, pool)

		// get available nodes in selected audio pool
		val availablePoolNodes = audioClient
			.getNodes(onlyAvailable = true)
			.filter { it.pool == pool }

		if (availablePoolNodes.isEmpty()) {
			// if not any node found, skipping transfer
			return false
		}
		// assign new future event at every node transfer
		audioClient.voiceGatewayUpdateTrigger = CompletableFuture()

		if (gatewayVoiceStateInterceptor.inAudioChannel(selfMember) == false) {
			// if bot is not in audio channel, connect to audio channel and update voice gateway
			// endpoint in JDAVoiceUpdateListener class
			authorMember.voiceState?.channel?.let {
				gatewayVoiceStateInterceptor.makeConnect(guild, it)
				log.debug("Connect with audio channel: {} in guild: {}.", guildId, it)
			}
		} else {
			log.debug("Already connected in audio channel. Skipping updating voice gateway server.")
			audioClient.voiceGatewayUpdateTrigger?.complete(null)
		}
		// invoke async, when onVoiceServerUpdate method sets new voice gateway endpoint
		audioClient.voiceGatewayUpdateTrigger?.thenRun {
			audioClient.transferNodeFromNewPool(guildId, pool, onTransferNode)
			audioClient.voiceGatewayUpdateTrigger = null
		}
		return true
	}

	fun disconnectWithAudioChannel(guild: Guild) {
		gatewayVoiceStateInterceptor.disconnect(guild)
		log.debug("Disconnect with audio channel in guild: {}.", guild.idLong)
	}
}
