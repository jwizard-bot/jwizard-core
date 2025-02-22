package pl.jwizard.jwc.core.server

import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import org.eclipse.jetty.http.HttpHeader
import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.property.BotProperty
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.server.filter.WebFilterBase
import pl.jwizard.jwl.server.filter.WebFilterType

@Component
class AuthenticationFilter(environment: BaseEnvironment) : WebFilterBase {
	override val matcher = "/api/*"
	override val type = WebFilterType.BEFORE

	private val token = environment.getProperty<String>(BotProperty.SERVICE_REST_API_TOKEN)

	override fun filter(ctx: Context) {
		val passedToken = ctx.req().getHeader(HttpHeader.AUTHORIZATION.asString())
		if (passedToken != token) {
			throw UnauthorizedResponse()
		}
	}
}
