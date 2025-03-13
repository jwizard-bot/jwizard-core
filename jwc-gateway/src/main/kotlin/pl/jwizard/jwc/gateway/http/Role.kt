package pl.jwizard.jwc.gateway.http

import io.javalin.security.RouteRole

enum class Role : RouteRole {
	AUTHENTICATED,
	;
}
