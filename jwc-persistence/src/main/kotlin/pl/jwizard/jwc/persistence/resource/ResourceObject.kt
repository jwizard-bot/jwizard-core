/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource

/**
 * This enum class represents resource objects.
 *
 * Each constant corresponds to a specific resource (ex. images), identified by its path. The [resourcePath] property
 * holds the relative path to the resource.
 *
 * Defining following properties:
 *
 * - [LOGO]: Represents the logo resource.
 * - [BANNER]: Represents the Discord banner resource.
 * - [RADIO_STATION]: Represents the path to a radio station's thumbnail image.
 *
 * @property resourcePath The relative path to the resource in the S3 storage.
 * @author Miłosz Gilga
 */
enum class ResourceObject(val resourcePath: String) {

	/**
	 * Represents the logo resource.
	 */
	LOGO("brand/logo.png"),

	/**
	 * Represents the Discord banner resource.
	 */
	BANNER("brand/discord-banner.png"),

	/**
	 * Represents the path to a radio station's thumbnail image. The `%s` placeholder is replaced by the station's slug,
	 * which typically matches the station's identifier in the system.
	 *
	 * Example path: `brand/radio-station/{station-slug}.jpg`
	 */
	RADIO_STATION("brand/radio-station/%s.jpg"),
	;
}
