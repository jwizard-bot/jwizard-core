package pl.jwizard.jwc.core.property.guild

import pl.jwizard.jwc.core.i18n.source.I18nUtilSource
import pl.jwizard.jwc.core.util.secToDTF

enum class GuildPropertyConverter(
	val mapper: (Any) -> Any,
	val isI18nContent: Boolean = false,
) {
	// no convert
	BASE({ it }),

	// seconds to HH:mm:ss
	TO_DTF_SEC({ secToDTF(it as Long) }),

	// to percentage
	TO_PERCENTAGE({ "${it}%" }),

	// to on/off based on boolean value
	TO_BOOL({ if (it == true) I18nUtilSource.TURN_ON else I18nUtilSource.TURN_OFF }, true)
}
