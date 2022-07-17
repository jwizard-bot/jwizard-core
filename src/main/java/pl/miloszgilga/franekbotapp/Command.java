/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: Command.java
 * Last modified: 16/07/2022, 01:22
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

package pl.miloszgilga.franekbotapp;

import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
public enum Command {
    MUSIC_PLAY("play", "odtworzenie piosenki i dodanie do kolejki (jeśli jakaś jest w danej chwili odtwarzana)"),
    MUSIC_PAUSE("pause", "wstrzymanie odtwarzania aktualnie odtwarzanej piosenki"),
    MUSIC_RESUME("resume", "kontyuacja odtwarzania piosenki od momentu wstrzymania"),
    MUSIC_LOOP("loop", "zapętlenie aktualnie odtwarzanej piosenki"),
    MUSIC_VOLUME("volume", "zmiana głośności odtwarzacza w skali 0 - 150% (domyślna głośność odtwarzacza to 100%)"),
    MUSIC_SKIP("skip", "umożliwia autorowi piosenki pominięcie jej (aktualnie odtwarzanej) i przejście do następnej"),
    MUSIC_JOIN("join", "przeniesienie bota na kanał, na którym znajduje się użytkownik"),
    MUSIC_QUEUE("queue", "wyświelenie wszystkich piosenek w kolejce"),
    MUSIC_VOTE_SKIP("voteskip", "pominięcie aktualnie odtwarzanej piosenki z kolejki i odtworzenie nowej poprzez głosowanie"),
    MUSIC_VOTE_SHUFFLE("voteshuffle", "przetasowanie kolejki z piosenkami poprzez głosowanie"),
    MUSIC_VOTE_QUEUE_CLEAR("voteclqueue", "wyczyszczenie całej kolejki z piosenkami (nie usuwa aktualnie odtwarzającej) poprzez głosowanie"),
    HELP("help", "wyświelnie wszystkich komend bota"),
    HELP_ME("helpme", "wyświetlenie wszystkich komend bota w wiadomości prywatnej");

    private final String commandName;
    private final String commandDescription;

    public static List<String> getAllCommands() {
        return Arrays
                .stream(Command.values()).map(Command::getCommandName)
                .collect(Collectors.toList());
    }
}