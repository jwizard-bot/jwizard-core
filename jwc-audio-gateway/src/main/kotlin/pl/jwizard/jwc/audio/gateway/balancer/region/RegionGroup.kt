package pl.jwizard.jwc.audio.gateway.balancer.region

// voice regions groups in discord voice channels
enum class RegionGroup {
	ASIA,
	EUROPE,
	US,
	SOUTH_AMERICA,
	AFRICA,
	MIDDLE_EAST,
	UNKNOWN,
	;

	companion object {
		fun fromRawValue(region: String) = when (region.uppercase()) {
			"AFRICA" -> AFRICA
			"ASIA" -> ASIA
			"EUROPE" -> EUROPE
			"MIDDLE_EAST" -> MIDDLE_EAST
			"SOUTH_AMERICA" -> SOUTH_AMERICA
			"US" -> US
			else -> UNKNOWN
		}
	}
}
