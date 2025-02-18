package pl.jwizard.jwc.audio.client

import pl.jwizard.jwl.vault.kvgroup.VaultKvGroupPropertySource
import kotlin.reflect.KClass

enum class AudioNodeProperty(
	override val key: String,
	override val type: KClass<*> = String::class,
) : VaultKvGroupPropertySource {
	ACTIVE("V_ACTIVE", Boolean::class),
	NAME("V_NAME"),
	GATEWAY_HOST("V_GATEWAY_HOST"),
	PASSWORD("V_PASSWORD"),
	SECURE("V_SECURE", Boolean::class),
	NODE_POOL("V_NODE_POOL"),
	REGION_GROUP("V_REGION_GROUP"),
	;
}
