/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command

import java.util.concurrent.TimeUnit

data class DefferedEmbed(
	val duration: Long,
	val unit: TimeUnit
) {
	constructor() : this(0, TimeUnit.MICROSECONDS)
}
