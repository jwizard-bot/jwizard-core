/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractCacheableDao.java
 * Last modified: 25/04/2023, 16:02
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

import org.springframework.data.jpa.repository.JpaRepository;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

abstract class AbstractCacheableDao<T, D extends JpaRepository<T, Long>> {

    protected final BotConfiguration config;
    protected final D cacheableRepository;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    AbstractCacheableDao(BotConfiguration config, D cacheableRepository) {
        this.config = config;
        this.cacheableRepository = cacheableRepository;
    }
}
