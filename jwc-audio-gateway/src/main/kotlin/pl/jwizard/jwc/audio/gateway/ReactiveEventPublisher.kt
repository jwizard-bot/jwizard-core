package pl.jwizard.jwc.audio.gateway

import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

// T - type of events that will be published
class ReactiveEventPublisher<T> {
	// emit events to multiple subscribers
	// sink is backpressure-aware and buffers events when necessary
	private val sink: Sinks.Many<T> = Sinks.many().multicast().onBackpressureBuffer()

	private val flux: Flux<T> = sink.asFlux()
	private val reference = flux.subscribe()

	fun publishWithException(value: T & Any) {
		try {
			sink.tryEmitNext(value)
		} catch (ex: Exception) {
			sink.emitError(ex, Sinks.EmitFailureHandler.FAIL_FAST)
		}
	}

	internal inline fun <reified V : Any> ofType(): Flux<V> = flux.ofType(V::class.java)

	fun dispose() = reference.dispose()
}
