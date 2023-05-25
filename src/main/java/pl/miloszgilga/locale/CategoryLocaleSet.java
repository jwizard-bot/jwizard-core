/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: CategoryLocaleSet.java
 * Last modified: 28/04/2023, 22:53
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

package pl.miloszgilga.locale;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import pl.miloszgilga.core.IEnumerableLocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Getter
@RequiredArgsConstructor
public enum CategoryLocaleSet implements IEnumerableLocaleSet {

    COMMAND_CATEGORY_MUSIC                          ("jwizard.command.category.Music"),
    COMMAND_CATEGORY_DJ_ROLE                        ("jwizard.command.category.DjRole"),
    COMMAND_CATEGORY_STATISTICS                     ("jwizard.command.category.Statistics"),
    COMMAND_CATEGORY_OWNER                          ("jwizard.command.category.Owner"),
    COMMAND_CATEGORY_OWNER_AND_MANAGER              ("jwizard.command.category.OwnerAndManager"),
    COMMAND_CATEGORY_VOTE                           ("jwizard.command.category.Voting"),
    COMMAND_CATEGORY_OTHERS                         ("jwizard.command.category.Others");

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final String holder;
}
