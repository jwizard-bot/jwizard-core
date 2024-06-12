/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.vote

import net.dv8tion.jda.api.entities.MessageEmbed
import kotlin.reflect.KClass

data class VoteResponseData(
	val initClazz: KClass<*>,
	val message: MessageEmbed,
	val onSuccess: (finishData: VoteFinishData) -> MessageEmbed,
	val onFailure: (finishData: VoteFinishData) -> MessageEmbed,
	val onTimeout: (finishData: VoteFinishData) -> MessageEmbed,
)
