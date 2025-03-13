package pl.jwizard.jwc.gateway.http

import io.javalin.http.Context
import pl.jwizard.jwl.server.route.handler.Handler

class BasicAuthRouteHandler(val callback: (Context) -> Unit) : Handler() {
	override val withRoles = mapOf(forAllRouteMethods(Role.AUTHENTICATED))

	override fun handle(ctx: Context) = callback(ctx)
}
