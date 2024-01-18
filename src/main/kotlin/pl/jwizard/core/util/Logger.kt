/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T : Any> logger(): Logger = LoggerFactory.getLogger(T::class.java)
