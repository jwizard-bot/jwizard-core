/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: JDAInjectableCommandLazyService.java
 * Last modified: 07/04/2023, 15:54
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

package pl.miloszgilga.core.loader;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Lazy
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JDAInjectableCommandLazyService {
}
