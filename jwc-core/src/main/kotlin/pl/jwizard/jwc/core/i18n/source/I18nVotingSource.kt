/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.i18n.source

import pl.jwizard.jwl.i18n.I18nLocaleSource

/**
 * This enum class represents objects stored in an S3-compatible storage service.
 *
 * @author Miłosz Gilga
 * @see I18nLocaleSource
 */
enum class I18nVotingSource(override val placeholder: String) : I18nLocaleSource {
	ON_SUCCESS_VOTING("jwc.voting.votingSuccess"),
	ON_FAILURE_VOTING("jwc.voting.votingFailure"),
	ON_TIMEOUT_VOTING("jwc.voting.votingTimeout"),
	MAX_TIME_VOTING("jwc.voting.maxVotingTime"),
	FIRST_RESULT("jwc.voting.firstResult"),
	RANDOM_RESULT("jwc.voting.randomResult"),
	PICK_AN_OPTION("jwc.voting.pickAnOption"),
	;
}
