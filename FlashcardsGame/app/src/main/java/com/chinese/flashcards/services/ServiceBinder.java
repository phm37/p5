package com.chinese.flashcards.services;

import android.app.Service;
import android.os.Binder;

public class ServiceBinder<T> extends Binder {

    private Service service;

    public ServiceBinder(Service s) {
        this.service = s;
    }

    public Service getService() {
        return this.service;
    }
}
