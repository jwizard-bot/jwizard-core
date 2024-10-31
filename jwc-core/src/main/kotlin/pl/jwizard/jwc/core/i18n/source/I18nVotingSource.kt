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
	ON_SUCCESS_VOTING("jw.voting.votingSuccess"),
	ON_FAILURE_VOTING("jw.voting.votingFailure"),
	ON_TIMEOUT_VOTING("jw.voting.votingTimeout"),
	MAX_TIME_VOTING("jw.voting.maxVotingTime"),
	FIRST_RESULT("jw.voting.firstResult"),
	RANDOM_RESULT("jw.voting.randomResult"),
	PICK_AN_OPTION("jw.voting.pickAnOption"),
	;
}
