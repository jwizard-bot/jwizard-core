package pl.jwizard.jwc.gateway.http

import pl.jwizard.jwl.server.filter.FilterRole

enum class Role : FilterRole {
	AUTHENTICATED,
	;

	override val id
		get() = name
}
