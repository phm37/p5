package com.chinese.flashcards.models;

import android.content.Context;

public class ApplicationContext {
    private Context context;

    public ApplicationContext(Context c) {
        this.context = c;
    }

    public Context getContext() {
        return this.context;
    }
}
