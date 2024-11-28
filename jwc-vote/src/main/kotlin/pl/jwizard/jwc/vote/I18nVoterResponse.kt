/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.vote

import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.vote.I18nVoterResponse.Builder

/**
 * This class represents a localized voter response with initial and failure messages,
 * along with a payload of generic type [T].
 *
 * @param T The type of payload associated with the voting process.
 * @property initMessage The initial message to be shown at the start of voting, localized with arguments.
 * @property failedMessage The failure message to be shown when voting fails, localized with arguments.
 * @property payload The data or content that is associated with the vote, of generic type [T].
 * @author Miłosz Gilga
 * @see Builder
 */
class I18nVoterResponse<T : Any> private constructor(
	val initMessage: I18nMessageWithArgs<I18nResponseSource>,
	val failedMessage: I18nMessageWithArgs<I18nResponseSource>,
	val payload: T,
) {

	/**
	 * Builder class for constructing instances of [I18nVoterResponse].
	 *
	 * @param T The type of payload that will be associated with the voting process.
	 */
	class Builder<T : Any> {

		/**
		 * The initial message to be set for the voting process.
		 */
		private lateinit var initMessage: I18nMessageWithArgs<I18nResponseSource>

		/**
		 * The failure message to be set in case the voting process fails.
		 */
		private lateinit var failedMessage: I18nMessageWithArgs<I18nResponseSource>

		/**
		 * The payload to be associated with the voting process.
		 */
		private lateinit var payload: T

		/**
		 * Sets the initial message to be displayed during the voting process.
		 *
		 * @param i18nLocaleSource The localized message source for the initial message.
		 * @param args The optional arguments for the localized message, defaults to an empty map.
		 * @return The builder instance, allowing method chaining.
		 */
		fun setInitMessage(i18nLocaleSource: I18nResponseSource, args: Map<String, Any?> = emptyMap()) = apply {
			initMessage = I18nMessageWithArgs(i18nLocaleSource, args)
		}

		/**
		 * Sets the failure message to be displayed if the voting process fails.
		 *
		 * @param i18nLocaleSource The localized message source for the failure message.
		 * @param args The optional arguments for the localized message, defaults to an empty map.
		 * @return The builder instance, allowing method chaining.
		 */
		fun setFailedMessage(i18nLocaleSource: I18nResponseSource, args: Map<String, Any?> = emptyMap()) = apply {
			failedMessage = I18nMessageWithArgs(i18nLocaleSource, args)
		}

		/**
		 * Sets the payload that will be associated with the voting process.
		 *
		 * @param payload The data or content to be processed after the vote.
		 * @return The builder instance, allowing method chaining.
		 */
		fun setPayload(payload: T) = apply { this.payload = payload }

		/**
		 * Builds the [I18nVoterResponse] instance using the provided initial message, failure message, and payload.
		 *
		 * @return The constructed [I18nVoterResponse] instance.
		 */
		fun build() = I18nVoterResponse(initMessage, failedMessage, payload)
	}
}
