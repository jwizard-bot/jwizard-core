/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.client

import pl.jwizard.jwac.node.NodePool

/**
 * Represents the types of audio node pools available in the system.
 *
 * @author Miłosz Gilga
 */
enum class AudioNodePool : NodePool {

	/**
	 * A queued audio node pool (normal tracks).
	 */
	QUEUED,

	/**
	 * A continuous audio node pool (online radio).
	 */
	CONTINUOUS,
	;

	override val poolName = name
}
