package pl.jwizard.jwc.audio.gateway.link

import dev.arbjerg.lavalink.protocol.v4.VoiceState
import pl.jwizard.jwc.audio.gateway.node.AudioNode
import pl.jwizard.jwc.audio.gateway.node.NodePool
import pl.jwizard.jwl.util.logger
import java.time.Duration

class Link(
	val guildId: Long,
	audioNode: AudioNode,
) {
	companion object {
		private val log = logger<Link>()
	}

	private var state = LinkState.DISCONNECTED

	internal var selectedNode = audioNode
		private set

	val cachedPlayer
		get() = selectedNode.getCachedPlayer(guildId)

	val player
		get() = selectedNode.getPlayer(guildId)

	fun destroy() = selectedNode.destroyPlayerAndLink(guildId)

	fun createOrUpdatePlayer() = selectedNode.createOrUpdatePlayer(guildId)

	internal fun updateState(state: LinkState) {
		this.state = state
	}

	internal fun transferNode(newNode: AudioNode) {
		state = LinkState.CONNECTING
		val player = selectedNode.getCachedPlayer(guildId)
		if (player != null) {
			newNode.createOrUpdatePlayer(guildId)
				.applyBuilder(player.stateToBuilder())
				.delaySubscription(Duration.ofMillis(1000))
				.subscribe(
					{ selectedNode.removeCachedPlayer(guildId) },
					{
						state = LinkState.DISCONNECTED
						log.error(
							"(link: {}) Failed to transfer player to new node: {}. Cause: {}.",
							this,
							newNode,
							it.message
						)
					}
				)
		}
		selectedNode = newNode
	}

	internal fun transferToPool(
		newNode: AudioNode,
		newPool: NodePool,
		afterSetNode: (AudioNode) -> Unit
	) {
		val player = selectedNode.getCachedPlayer(guildId)
		val playerBuilder = newNode.createOrUpdatePlayer(guildId)
		player?.let {
			playerBuilder.setVolume(it.volume)
			playerBuilder.setVoiceState(it.voiceState)
			playerBuilder.setFilters(it.filters)
		}
		selectedNode.destroyPlayer(guildId).subscribe()
		playerBuilder
			.delaySubscription(Duration.ofMillis(1000))
			.subscribe(
				{ afterSetNode(newNode) },
				{
					state = LinkState.DISCONNECTED
					log.error("(link: {}) Failed to transfer player to new node pool: {}.", this, newPool)
				},
			)
		selectedNode = newNode
	}

	internal fun updateNodeVoiceState(newVoiceState: VoiceState) {
		if (!selectedNode.available) {
			return
		}
		state = LinkState.CONNECTING
		selectedNode.createOrUpdatePlayer(guildId)
			.setVoiceState(newVoiceState)
			.subscribe(
				{ log.debug("(link: {}) Updated voice state: {}.", this, newVoiceState) },
				{
					state = LinkState.DISCONNECTED
					log.error(
						"(link: {}) Failed update voice state to: {}. Cause: {}.",
						this,
						newVoiceState,
						it.message
					)
				}
			)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) {
			return true
		}
		if (javaClass != other?.javaClass) {
			return false
		}
		return guildId == (other as Link).guildId
	}

	override fun hashCode() = guildId.hashCode()

	override fun toString() = "$selectedNode (guildId: $guildId)"
}
