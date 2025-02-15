package pl.jwizard.jwc.audio.gateway.balancer.penalty

// used for tracking and penalizing certain events in the audio balancer
internal enum class MetricType {
	TRACK_STUCK,
	TRACK_EXCEPTION,
	LOAD_FAILED,
	LOAD_ATTEMPT,
	;
}
