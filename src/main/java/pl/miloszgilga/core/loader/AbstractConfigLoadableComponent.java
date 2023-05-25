/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractConfigLoadableComponent.java
 * Last modified: 28/03/2023, 23:49
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractConfigLoadableComponent {

    private boolean alreadyLoaded = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void loadConfiguration(Object... params) {
        try {
            abstractLoadConfiguration(params);
            alreadyLoaded = true;
        } catch (Exception ex) {
            alreadyLoaded = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initializeComponent() {
        if (!alreadyLoaded) {
            throw new RuntimeException("Configuration not loaded. Run loadConfiguration() before executing action.");
        }
        abstractInitializeComponent();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void abstractLoadConfiguration(Object... params);
    protected abstract void abstractInitializeComponent();
}
