/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bot.properties

import org.apache.commons.lang3.StringUtils

data class S3Properties(
	var host: String = StringUtils.EMPTY,
)
