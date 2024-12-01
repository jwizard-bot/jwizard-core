/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.command.async

import pl.jwizard.jwc.command.context.GuildCommandContext

/**
 * Type alias for an asynchronous updatable hook that operates on a [GuildCommandContext].
 *
 * This alias simplifies the usage of [AsyncUpdatableHook] for the specific case where the hook operates on
 * [GuildCommandContext] as the context and `T` as the updatable data type.
 *
 * @param T The type of the updatable data used by the hook.
 * @author Miłosz Gilga
 */
typealias TUpdatableCommandHook<T> = AsyncUpdatableHook<GuildCommandContext, T>
