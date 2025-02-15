package pl.jwizard.jwc.audio.gateway.balancer.region

enum class VoiceRegion(
	val group: RegionGroup,
	val id: String,
	private val visibleName: String,
) {
	AMSTERDAM(RegionGroup.EUROPE, "amsterdam", "Amsterdam"),
	ATLANTA(RegionGroup.US, "atlanta", "Atlanta"),
	BRAZIL(RegionGroup.SOUTH_AMERICA, "brazil", "Brazil"),
	BUCHAREST(RegionGroup.EUROPE, "bucharest", "Bucharest"),
	BUENOS_AIRES(RegionGroup.SOUTH_AMERICA, "buenos-aires", "Brazil"),
	DUBAI(RegionGroup.MIDDLE_EAST, "dubai", "Dubai"),
	EUROPE(RegionGroup.EUROPE, "europe", "Europe"),
	FINLAND(RegionGroup.EUROPE, "finland", "Finland"),
	FRANKFURT(RegionGroup.EUROPE, "frankfurt", "Frankfurt"),
	HONGKONG(RegionGroup.ASIA, "hongkong", "Hong Kong"),
	INDIA(RegionGroup.ASIA, "india", "India"),
	JAPAN(RegionGroup.ASIA, "japan", "Japan"),
	LONDON(RegionGroup.EUROPE, "london", "London"),
	MADRID(RegionGroup.EUROPE, "madrid", "Madrid"),
	MILAN(RegionGroup.EUROPE, "milan", "Milan"),
	MONTREAL(RegionGroup.US, "montreal", "Montreal"),
	NEWARK(RegionGroup.US, "newark", "Newark"),
	OREGON(RegionGroup.US, "oregon", "Oregon"),
	ROTTERDAM(RegionGroup.EUROPE, "rotterdam", "Rotterdam"),
	RUSSIA(RegionGroup.EUROPE, "russia", "Russia"),
	SANTA_CLARA(RegionGroup.US, "santa-clara", "Santa Clara"),
	SANTIAGO(RegionGroup.SOUTH_AMERICA, "santiago", "Santiago"),
	SEATTLE(RegionGroup.US, "seattle", "Seattle"),
	SINGAPORE(RegionGroup.ASIA, "singapore", "Singapore"),
	SOUTH_AFRICA(RegionGroup.AFRICA, "southafrica", "South Africa"),
	SOUTH_KOREA(RegionGroup.ASIA, "south-korea", "South Korea"),
	ST_PETE(RegionGroup.US, "st-pete", "St Pete"),
	STOCKHOLM(RegionGroup.EUROPE, "stockholm", "Stockholm"),
	SYDNEY(RegionGroup.ASIA, "sydney", "Sydney"),
	TEL_AVIV(RegionGroup.MIDDLE_EAST, "tel-aviv", "Tel Aviv"),
	US_CENTRAL(RegionGroup.US, "us-central", "US Central"),
	US_EAST(RegionGroup.US, "us-east", "US East"),
	US_SOUTH(RegionGroup.US, "us-south", "US South"),
	US_WEST(RegionGroup.US, "us-west", "US West"),
	UNKNOWN(RegionGroup.UNKNOWN, "", "Unknown"),
	;

	companion object {
		fun fromEndpoint(endpoint: String): VoiceRegion {
			val endpointRegex = "^([a-z\\-]+)[0-9]+.*:443\$".toRegex()
			val match = endpointRegex.find(endpoint) ?: return UNKNOWN
			val idFromEndpoint = match.groupValues[1]
			return entries.find { it.id == idFromEndpoint } ?: UNKNOWN
		}
	}

	override fun toString() = visibleName
}
