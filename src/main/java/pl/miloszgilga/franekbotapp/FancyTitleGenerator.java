/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: FancyTitleGenerator.java
 * Last modified: 17/07/2022, 16:26
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


class FancyTitleGenerator {

    private static volatile FancyTitleGenerator fancyTitleGenerator;

    private FancyTitleGenerator() { }

    public void generateFancyTitle() {
        System.out.println();
        System.out.println("$$$$$$$$\\                                     $$\\             $$$$$$$\\             $$\\     ");
        System.out.println("$$  _____|                                    $$ |            $$  __$$\\            $$ |    ");
        System.out.println("$$ |    $$$$$$\\  $$$$$$\\  $$$$$$$\\   $$$$$$\\  $$ |  $$\\       $$ |  $$ | $$$$$$\\ $$$$$$\\   ");
        System.out.println("$$$$$\\ $$  __$$\\ \\____$$\\ $$  __$$\\ $$  __$$\\ $$ | $$  |      $$$$$$$\\ |$$  __$$\\\\_$$  _|  ");
        System.out.println("$$  __|$$ |  \\__|$$$$$$$ |$$ |  $$ |$$$$$$$$ |$$$$$$  /       $$  __$$\\ $$ /  $$ | $$ |    ");
        System.out.println("$$ |   $$ |     $$  __$$ |$$ |  $$ |$$   ____|$$  _$$<        $$ |  $$ |$$ |  $$ | $$ |$$\\ ");
        System.out.println("$$ |   $$ |     \\$$$$$$$ |$$ |  $$ |\\$$$$$$$\\ $$ | \\$$\\       $$$$$$$  |\\$$$$$$  | \\$$$$  |");
        System.out.println("\\__|   \\__|      \\_______|\\__|  \\__| \\_______|\\__|  \\__|      \\_______/  \\______/   \\____/");
        System.out.println();
    }

    public static synchronized FancyTitleGenerator getSingleton() {
        if (fancyTitleGenerator == null) {
            fancyTitleGenerator = new FancyTitleGenerator();
        }
        return fancyTitleGenerator;
    }
}