package com.chinese.flashcards.plugins;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.ApplicationContext;

public class CountUpTimer extends ApplicationContext {

    private long updateInterval;
    private long startTime;
    private long endTime;
    private boolean done;

    private Handler handler;
    private Runnable update;

    public CountUpTimer(Context c, long updateInterval) {
        super(c);
        this.updateInterval = updateInterval;
        this.done           = false;
        this.handler        = new Handler();
        this.update         = getUpdateRoutine();

    }

    public void start() {
        this.startTime = SystemClock.uptimeMillis();
        this.handler.postDelayed(update, updateInterval);
    }

    public void stop() {
        this.endTime = SystemClock.uptimeMillis();
        this.done    = true;
        invokeCallback(this.getElapsed());
    }

    public long getElapsed() {
        if (this.done)
            return (this.endTime - this.startTime);
        else
            return (SystemClock.uptimeMillis() - startTime);
    }

    private Runnable getUpdateRoutine() {
        return new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                invokeCallback(elapsed);
                CountUpTimer.this.handler.postDelayed(this, updateInterval);
            }
        };
    }

    private void invokeCallback(long elapsed) {
        Handler handler = new Handler(this.onTickCallback);
        Message message = new Message();
        Bundle timeBundle = new Bundle();
        timeBundle.putInt(getContext().getResources().getString(R.string.TimeElapsed), (int)elapsed);
        message.setData(timeBundle);
        handler.sendMessage(message);
    }

    public static String timeElapsedToClockString(long milliseconds) {
        long time = milliseconds / 1000;
        long seconds = time % 60;
        time /= 60;
        long minutes = time % 60;
        time /= 60;
        long hours = time % 24;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static String timeElapsedToString(long milliseconds) {
        long time = milliseconds / 1000;
        long seconds = time % 60;
        time /= 60;
        long minutes = time % 60;
        time /= 60;
        long hours = time % 24;

        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours + " h ");
        }
        if (minutes > 0) {
            timeString.append(minutes + " min ");
        }
        if (seconds > 0) {
            timeString.append(seconds + " s ");
        }

        return timeString.toString();
    }

    public Handler.Callback onTickCallback;
}
