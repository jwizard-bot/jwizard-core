package pl.jwizard.jwc.audio.gateway.balancer.penalty

internal enum class PenaltyFilteringResult(val penalty: Int) {
	// passing result with no penalty applied
	PASS(0),

	// moderate penalty applied
	SOFT_BLOCK(1000),

	// full block
	BLOCK(10000000),
	;
}
