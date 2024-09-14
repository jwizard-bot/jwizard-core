/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.property.extractor

import org.slf4j.LoggerFactory
import org.springframework.vault.authentication.TokenAuthentication
import org.springframework.vault.client.VaultEndpoint
import org.springframework.vault.core.VaultTemplate
import org.springframework.vault.support.VaultResponse
import java.util.*

/**
 * Extractor for property values from HashiCorp Vault.
 *
 * @property vaultServerUri The URI of the Vault server.
 * @property vaultToken The token used to authenticate with Vault.
 * @property vaultKvBackend The KV backend path.
 * @property vaultKvDefaultContext The default context for KV secrets.
 * @property vaultKvApplicationName The application name context for KV secrets.
 * @author Miłosz Gilga
 * @see PropertyValueExtractor
 */
class VaultPropertyValueExtractor(
	private val vaultServerUri: String,
	private val vaultToken: String,
	private val vaultKvBackend: String,
	private val vaultKvDefaultContext: String,
	private val vaultKvApplicationName: String,
) : PropertyValueExtractor<VaultPropertyValueExtractor>(VaultPropertyValueExtractor::class) {

	/**
	 * Template used to interact with Vault. This field holds an instance of [VaultTemplate] configured with the
	 * provided Vault server URI and authentication token.
	 */
	private val vaultTemplate: VaultTemplate

	companion object {
		private val log = LoggerFactory.getLogger(VaultPropertyValueExtractor::class.java)
	}

	init {
		log.info("Connecting with vault KV server: {}.", vaultServerUri)

		val vaultEndpoint = VaultEndpoint.from(vaultServerUri)
		vaultTemplate = VaultTemplate(vaultEndpoint, TokenAuthentication(vaultToken))
	}

	/**
	 * Retrieves properties from Vault and combines them into a single map.
	 *
	 * This method reads secrets from two paths in Vault: one specified by [vaultKvDefaultContext] and another by
	 * [vaultKvApplicationName]. The results from both paths are merged into a single map of properties.
	 *
	 * @return A map of properties where keys are property names and values are property values.
	 */
	override fun setProperties(): Map<Any, Any> {
		return readKvSecrets(vaultKvDefaultContext) + readKvSecrets(vaultKvApplicationName)
	}

	/**
	 * Reads secrets from a KV store in Vault.
	 *
	 * @param kvStore The KV store path.
	 * @return A map of secrets read from the KV store.
	 */
	private fun readKvSecrets(kvStore: String): Properties {
		val properties = Properties()
		val qualifiedKvStorePath = "$vaultKvBackend/$kvStore"
		val kvSecrets: VaultResponse? = vaultTemplate.read(qualifiedKvStorePath)

		kvSecrets?.let { response ->
			response.data?.forEach { properties[it.key] = it.value }
			log.info("Load: {} secrets from: {} KV store.", response.data?.size, qualifiedKvStorePath)
		} ?: run {
			log.warn("Not found any secrets in KV store: {}. Skipping.", qualifiedKvStorePath)
		}
		return properties
	}

	override val extractionKey = "vault"
}
