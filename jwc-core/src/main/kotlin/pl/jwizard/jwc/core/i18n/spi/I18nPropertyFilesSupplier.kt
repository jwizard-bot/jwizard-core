/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.spi

import java.nio.charset.Charset

/**
 * Interface for supplying raw content from internationalization (i18n) property files. This is used to retrieve
 * content in different languages based on the specified bundle and charset.
 *
 * @author Miłosz Gilga
 */
interface I18nPropertyFilesSupplier {

	/**
	 * Retrieves the raw content of a specific property file for a given language and charset.
	 *
	 * @param charset The character set to use when decoding the property file content.
	 * @param remoteBundle The name of the remote bundle where the property file is located.
	 * @param language The language code (e.g., "en", "pl") for the property file to be retrieved.
	 * @return The raw content of the property file as a [String], or `null` if the file could not be retrieved.
	 */
	fun getFileRaw(remoteBundle: String, language: String, charset: Charset): String?
}
