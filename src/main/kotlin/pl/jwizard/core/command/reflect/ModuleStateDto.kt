/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.command.reflect

import org.apache.commons.lang3.StringUtils

data class ModuleStateDto(
	val name: String,
	val isEnabled: Boolean,
) {
	constructor() : this(StringUtils.EMPTY, false)
}