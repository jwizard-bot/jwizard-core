/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: AvailableCommands.java
 * Last modified: 11/07/2022, 22:02
 * Project name: franek-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga;

import lombok.Getter;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;


@Getter
public enum Command {
    MUSIC_PLAY("play", "odtworzenie piosenki i dodanie do kolejki (jeśli jakaś jest w danej chwili odtwarzana)"),
    MUSIC_SKIP("skip", "pominięcie aktualnie odtwarzanej piosenki z kolejki i odtworzenie nowej poprzez głosowanie"),
    MUSIC_QUEUE("queue", "wyświelenie wszystkich piosenek w kolejce"),
    MUSIC_LOOP("loop", "zapętlenie aktualnie odtwarzanej piosenki"),
    MUSIC_JOIN("join", "przeniesienie bota na kanał, na którym znajduje się użytkownik"),
    MUSIC_SHUFFLE("shuffle", "przetasowanie kolejki z piosenkami"),
    HELP("help", "wyświelnie wszystkich komend bota"),
    HELP_ME("helpme", "wyświetlenie wszystkich komend bota w wiadomości prywatnej");

    private final String commandName;
    private final String commandDescription;

    Command(String commandName, String commandDescription) {
        this.commandName = commandName;
        this.commandDescription = commandDescription;
    }

    public static List<String> getAllCommands() {
        return Arrays
                .stream(Command.values()).map(Command::getCommandName)
                .collect(Collectors.toList());
    }
}