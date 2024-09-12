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

	/**
	 * Represents the path to internationalization (i18n) resource files stored in the S3 bucket. The path includes
	 * placeholders for the module name and language code, which are replaced with specific values to access the
	 * appropriate localized configuration file.
	 *
	 * The first `%s` is replaced by the module name (ex. *i18n-commands*), and the second `%s` is replaced by
	 * the language code (ex. *en*).
	 *
	 * Example usage: `configuration/ui_en.properties`
	 */
	I18N_BUNDLE("configuration/%s_%s.properties")
	;
}
