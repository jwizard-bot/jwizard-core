package pl.jwizard.jwc.core.property

import pl.jwizard.jwl.property.AppProperty
import kotlin.reflect.KClass

// separated link fragment properties, only for link elements
enum class LinkFragmentProperty(
	override val key: String,
	override val type: KClass<*> = String::class,
) : AppProperty {
	LINK_FRAGMENT_COMMAND("link.fragment.command"),
	LINK_FRAGMENT_ERROR_CODE("link.fragment.error-code"),
	;
}
