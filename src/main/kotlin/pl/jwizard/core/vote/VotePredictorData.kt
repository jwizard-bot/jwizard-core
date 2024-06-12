/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.vote

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

data class VotePredictorData(
	val forYes: AtomicInteger,
	val forNo: AtomicInteger,
	val required: AtomicInteger,
	val succeed: AtomicBoolean,
	val votedUsers: MutableList<User>,
	val response: VoteResponseData,
	val message: Message,
)
