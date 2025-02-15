package pl.jwizard.jwc.persistence.resource

// example path: /<default static path>/radio-station/{station-slug}.jpg
enum class ResourceObject(val resourcePath: String) {
	RADIO_STATION("radio-station/%s.jpg"),
	;
}
