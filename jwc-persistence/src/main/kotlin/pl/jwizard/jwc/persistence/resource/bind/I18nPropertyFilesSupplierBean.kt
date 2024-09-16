/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.persistence.resource.bind

import org.springframework.stereotype.Component
import pl.jwizard.jwc.core.i18n.spi.I18nPropertyFilesSupplier
import pl.jwizard.jwc.persistence.resource.ResourceObject
import pl.jwizard.jwc.persistence.resource.S3ResourceRetrieverBean
import java.nio.charset.Charset

/**
 * Implementation of [I18nPropertyFilesSupplier] that retrieves i18n property files from S3.
 *
 * @property s3ResourceRetrieverBean The [S3ResourceRetrieverBean] used to interact with the S3 storage service.
 * @author Miłosz Gilga
 */
@Component
class I18nPropertyFilesSupplierBean(
	private val s3ResourceRetrieverBean: S3ResourceRetrieverBean
) : I18nPropertyFilesSupplier {

	/**
	 * Retrieves a property file from S3 as a raw text string.
	 *
	 * @param remoteBundle The bundle name of the property file.
	 * @param language The language tag for the property file (ex. *en*, *pl*).
	 * @param charset The character set to use when decoding the content of the property file.
	 * @return The content of the property file as a [String], or `null` if the file could not be retrieved or read.
	 */
	override fun getFileRaw(remoteBundle: String, language: String, charset: Charset) =
		s3ResourceRetrieverBean.getObjectAsText(ResourceObject.I18N_BUNDLE, charset, remoteBundle, language)
}
