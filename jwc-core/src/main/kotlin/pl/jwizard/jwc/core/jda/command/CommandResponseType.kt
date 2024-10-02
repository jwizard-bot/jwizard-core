/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.core.jda.command

import java.util.concurrent.CompletableFuture

/**
 * A type alias for a [CompletableFuture] that encapsulates a [CommandResponse].
 *
 * @author Miłosz Gilga
 */
typealias TFutureResponse = CompletableFuture<CommandResponse>
