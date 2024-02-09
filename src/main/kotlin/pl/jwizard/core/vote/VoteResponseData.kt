/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.vote

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User

data class VoteResponseData(
	val initClazz: KClass<*>,
	val message: MessageEmbed,
	val onSuccess: (finishData: VoteFinishData) -> MessageEmbed,
	val onFailure: (finishData: VoteFinishData) -> MessageEmbed,
	val onTimeout: (finishData: VoteFinishData) -> MessageEmbed,
)

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

data class VotePredictorData(
	val forYes: AtomicInteger,
	val forNo: AtomicInteger,
	val required: AtomicInteger,
	val succeed: AtomicBoolean,
	val votedUsers: MutableList<User>,
	val response: VoteResponseData,
	val message: Message,
)
