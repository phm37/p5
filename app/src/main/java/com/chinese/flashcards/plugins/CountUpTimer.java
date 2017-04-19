package com.chinese.flashcards.plugins;

import android.os.CountDownTimer;
import android.os.SystemClock;

public abstract class CountUpTimer extends CountDownTimer {

    private long startTime;
    private long endTime;

    public CountUpTimer(long updateInterval) {
        super(Long.MAX_VALUE, updateInterval);
        this.startTime = SystemClock.uptimeMillis();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long elapsed = SystemClock.uptimeMillis() - startTime;
        onTick((int)elapsed);
    }

    @Override
    public void onFinish() {
        this.endTime = SystemClock.uptimeMillis();
        long elapsed = this.endTime - startTime;
        onTick((int)elapsed);
    }

    public abstract void onTick(int milliSeconds);
}
