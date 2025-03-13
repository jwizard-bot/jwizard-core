package pl.jwizard.jwc.gateway.http

import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.RouteRole
import org.eclipse.jetty.http.HttpHeader
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.server.definedHeader
import pl.jwizard.jwl.server.filter.RoleFilterBase

@Component
class BasicAuthenticationFilter(environment: BaseEnvironment) : RoleFilterBase() {
	private val token = environment.getProperty<String>(BotProperty.SERVICE_REST_API_TOKEN)

	override val roles = arrayOf<RouteRole>(Role.AUTHENTICATED)

	override fun roleFilter(ctx: Context) {
		val passedToken = ctx.definedHeader(HttpHeader.AUTHORIZATION)
		if (passedToken != token) {
			throw UnauthorizedResponse()
		}
	}
}
