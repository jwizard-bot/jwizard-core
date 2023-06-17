/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: IBotCommandProxy.java
 * Last modified: 6/8/23, 9:41 PM
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

package pl.miloszgilga.command_proxy;

import java.util.function.Function;

import org.jmpsl.core.db.AbstractAuditableEntity;

import pl.miloszgilga.BotCommand;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public interface IBotCommandProxy<T extends AbstractAuditableEntity> {
    BotCommand getCommand();
    String getUid();
    String getCacheProxyName();
    Function<T, Boolean> getIsEnabled();
    IBiSupplier<T, Boolean> getSetValue();
}
