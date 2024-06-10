/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.vote

data class VoteFinishData(
	val forYes: Int,
	val forNo: Int,
	val total: Int,
	val required: Int
) {
	constructor(votePredictorData: VotePredictorData) : this(
		forYes = votePredictorData.forYes.get(),
		forNo = votePredictorData.forNo.get(),
		total = votePredictorData.forYes.addAndGet(votePredictorData.forNo.get()),
		required = votePredictorData.required.get()
	)
}
