/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.lava

import dev.arbjerg.lavalink.client.NodeOptions
import dev.arbjerg.lavalink.client.loadbalancing.IRegionFilter

/**
 * Data class representing a Lavalink node, which contains the necessary information to connect to and authenticate
 * with the node.
 *
 * @property name The name of the Lavalink node.
 * @property regionGroup The region filter associated with the node for load balancing.
 * @property nodeToken Authentication token for the Lavalink node.
 * @property hostUrl The URL where the node is hosted.
 * @author Miłosz Gilga
 */
data class LavaNode(
	val name: String,
	val regionGroup: IRegionFilter,
	val nodeToken: String,
	val hostUrl: String,
) {

	/**
	 * Converts this LavaNode into a NodeOptions object, which is used by the Lavalink client to establish a connection
	 * to the node.
	 *
	 * @param timeout The timeout (in milliseconds) for HTTP connections to the node.
	 * @return A fully built NodeOptions object for Lavalink client usage.
	 */
	fun toNodeOption(timeout: Long) = NodeOptions.Builder()
		.setName(name)
		.setServerUri(hostUrl)
		.setPassword(nodeToken)
		.setRegionFilter(regionGroup)
		.setHttpTimeout(timeout)
		.build()

	/**
	 * Provides a string representation of the LavaNode, which includes the name and host URL.
	 *
	 * @return A string in the format "name::hostUrl".
	 */
	override fun toString() = "$name::$hostUrl"
}
