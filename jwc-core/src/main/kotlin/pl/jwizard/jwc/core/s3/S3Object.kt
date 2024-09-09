/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.s3

import pl.jwizard.jwc.core.s3.S3Object.BANNER
import pl.jwizard.jwc.core.s3.S3Object.LOGO

/**
 * This enum class represents objects stored in an S3-compatible storage service.
 *
 * Each constant corresponds to a specific resource (ex. images) within the S3 bucket, identified by its path.
 * The [resourcePath] property holds the relative path to the resource within the bucket.
 *
 * Defining following properties:
 *
 * - [LOGO]: Represents the logo resource within the S3 bucket.
 * - [BANNER]: Represents the Discord banner resource within the S3 bucket.
 *
 * @property resourcePath The relative path to the resource in the S3 storage.
 * @author Miłosz Gilga
 */
enum class S3Object(val resourcePath: String) {

	/**
	 * Represents the logo resource within the S3 bucket.
	 */
	LOGO("brand/logo.png"),

	/**
	 * Represents the Discord banner resource within the S3 bucket.
	 */
	BANNER("brand/discord-banner.png"),
	;
}
