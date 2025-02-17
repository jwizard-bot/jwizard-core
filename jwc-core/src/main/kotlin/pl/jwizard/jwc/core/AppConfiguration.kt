package pl.jwizard.jwc.core

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pl.jwizard.jwl.i18n.I18n
import pl.jwizard.jwl.i18n.I18nInitializer
import pl.jwizard.jwl.ioc.IoCKtContextFactory
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.server.HttpServer
import pl.jwizard.jwl.server.exception.UnspecifiedExceptionAdvisor
import pl.jwizard.jwl.server.filter.LanguageHeaderExtractorFilter
import java.net.http.HttpClient

@Component
internal class AppConfiguration {
	@Bean
	fun objectMapper(): ObjectMapper {
		val objectMapper = ObjectMapper()
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
		// mappers
		objectMapper.registerModule(JavaTimeModule())
		// formatters
		objectMapper.dateFormat = StdDateFormat()
		return objectMapper
	}

	@Bean
	fun httpClient(): HttpClient = HttpClient.newHttpClient()

	@Bean
	fun environment() = BaseEnvironment()

	@Bean
	fun messageSource(): MessageSource {
		val i18nInitializer = I18nInitializer()
		return i18nInitializer.createMessageSource()
	}

	@Bean
	fun i18n(
		messageSource: MessageSource,
		environment: BaseEnvironment,
	) = I18n(messageSource, environment)

	@Bean
	fun httpServer(
		environment: BaseEnvironment,
		ioCKtContextFactory: IoCKtContextFactory,
		i18n: I18n,
		objectMapper: ObjectMapper,
	) = HttpServer(environment, ioCKtContextFactory, i18n, objectMapper)

	@Bean
	fun unspecifiedExceptionAdvisor(i18n: I18n) = UnspecifiedExceptionAdvisor(i18n)

	@Bean
	fun languageHeaderExtractorFilter(
		environment: BaseEnvironment,
	) = LanguageHeaderExtractorFilter(environment)
}
