package com.chinese.flashcards.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class ServiceConnection<T> implements android.content.ServiceConnection {

    private Service  service;
    private boolean  isBound;

    public ServiceConnection() {
        this.service = null;
        this.isBound = false;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        this.service = ((ServiceBinder)binder).getService();
        this.isBound = true;

        Handler handler = new Handler(this.onServiceConnectedCallback);
        handler.sendMessage(new Message());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.isBound = false;
    }

    public boolean isBound() {
        return this.isBound;
    }

    public T getService() {
        return (T)this.service;
    }

    public Handler.Callback onServiceConnectedCallback;
}
