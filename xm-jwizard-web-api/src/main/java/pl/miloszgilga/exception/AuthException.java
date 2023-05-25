/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AuthException.java
 * Last modified: 07/04/2023, 15:02
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

package pl.miloszgilga.exception;

import org.springframework.http.HttpStatus;

import org.jmpsl.core.exception.RestServiceAuthServerException;

import pl.miloszgilga.i18n.AppLocaleSet;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class AuthException {

    public static class UserNotFoundException extends RestServiceAuthServerException {
        public UserNotFoundException() {
            super(HttpStatus.NOT_FOUND, AppLocaleSet.USERNAME_NOT_FOUND_EXC);
        }
    }
}
