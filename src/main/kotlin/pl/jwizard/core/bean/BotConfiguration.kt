/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.core.bean

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import pl.jwizard.core.command.embed.EmbedBuilderService
import pl.jwizard.core.i18n.LangResourceService
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Primary
@Configuration
class BotConfiguration(
	private val langResourceService: LangResourceService,
	private val embedBuilderService: EmbedBuilderService,
) {
	val threadPool: ScheduledExecutorService
		get() = Executors.newSingleThreadScheduledExecutor { r ->
			val thread = Thread(r)
			thread.isDaemon = true
			thread
		}

	val langResourceServiceBean get() = langResourceService
	val embedBuilderServiceBean get() = embedBuilderService
}
