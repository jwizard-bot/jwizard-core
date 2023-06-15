/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CachedCommandStateData.java
 * Last modified: 6/15/23, 6:45 PM
 * Project name: jwizard-discord-bot
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *     <http://www.apache.org/license/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the license.
 */

package pl.miloszgilga.cacheable;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.command_proxy.IBotCommandProxy;
import pl.miloszgilga.domain.ICacheCommandRepository;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

record CachedCommandStateData<T extends AbstractAuditableEntity>(
    ICacheCommandRepository<T> commandRepository,
    BotCommand passedCommand,
    IBotCommandProxy<T>[] allValues
) {
}
