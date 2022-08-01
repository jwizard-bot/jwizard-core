/*
 * Copyright (c) 2022 by MILOSZ GILGA <https://miloszgilga.pl>
 *
 * File name: ExecutorTimer.java
 * Last modified: 24/07/2022, 22:05
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

package pl.miloszgilga.franekbotapp.executorhandlers;

import java.util.Timer;
import java.util.TimerTask;


public class ExecutorTimer {

    private final Timer timer = new Timer();
    private final long executingTime;
    private final TimerTask timerTask;
    private boolean isCancel;

    public ExecutorTimer(byte executingTimeInMinutes, IExecutorTimerLambaExpression expression) {
        executingTime = executingTimeInMinutes * 60 * 1000;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                expression.executeContentAfterTimeElapse();
            }
        };
    }

    public void execute() {
        isCancel = false;
        timer.schedule(timerTask, executingTime);
    }

    public void interrupt() {
        if (isCancel) return;
        isCancel = true;
        timerTask.cancel();
        timer.cancel();
    }
}