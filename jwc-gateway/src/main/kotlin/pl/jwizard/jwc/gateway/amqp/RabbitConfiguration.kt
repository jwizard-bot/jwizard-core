package pl.jwizard.jwc.gateway.amqp

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import pl.jwizard.jwl.property.BaseEnvironment
import pl.jwizard.jwl.rabbitmq.RabbitMqServer
import pl.jwizard.jwl.rabbitmq.RabbitQueue

@Component
class RabbitConfiguration {
	@Bean
	fun rabbitMqServer(environment: BaseEnvironment, objectMapper: ObjectMapper): RabbitMqServer {
		val rabbitServer = RabbitMqServer(objectMapper, environment)

		// queues
		rabbitServer.addQueue(RabbitQueue.JW_CORE_TO_API_CMD_STATS)
		rabbitServer.addQueue(RabbitQueue.JW_CORE_TO_API_LISTEN_STATS)

		return rabbitServer
	}
}
