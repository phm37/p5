package com.chinese.flashcards.models;

import android.content.Context;
import android.os.SystemClock;

import com.chinese.flashcards.R;

import java.io.Serializable;

public class Card extends ApplicationContext implements Serializable {

    public final String chinese;
    public final String pinyin;
    public final String english;

    private int triesCount;

    private int chineseSuccessCount;
    private int chineseFailureCount;
    private int pinyinSuccessCount;
    private int pinyinFailureCount;
    private int englishSuccessCount;
    private int englishFailureCount;

    private long chineseLastQuizDate;
    private long pinyinLastQuizDate;
    private long englishLastQuizDate;

    public Card(Context c, String chinese, String pinyin, String english) {
        super(c);
        this.chinese = chinese;
        this.pinyin  = pinyin;
        this.english = english;
    }

//    public int getSuccessCount() {
//        return this.successCount;
//    }
//
//    public int getFailureCount() {
//        return this.failureCount;
//    }

    public void setSuccessed(String mode, boolean isSuccess) {

        // Get Language Modes from resources
        String englishMode = this.getContext().getResources().getString(R.string.EnglishMode);
        String chineseMode = this.getContext().getResources().getString(R.string.ChineseMode);
        String pinyinMode  = this.getContext().getResources().getString(R.string.PinyinMode);

        if (mode.equalsIgnoreCase(englishMode)) {
            this.englishSuccessCount += isSuccess ? 1 : 0;
            this.englishFailureCount += isSuccess ? 0 : 1;
            this.englishLastQuizDate = SystemClock.uptimeMillis();
        }
        else if (mode.equalsIgnoreCase(chineseMode)) {
            this.chineseSuccessCount += isSuccess ? 1 : 0;
            this.chineseFailureCount += isSuccess ? 0 : 1;
            this.chineseLastQuizDate = SystemClock.uptimeMillis();
        }
        else if (mode.equalsIgnoreCase(pinyinMode)) {
            this.pinyinSuccessCount += isSuccess ? 1 : 0;
            this.pinyinFailureCount += isSuccess ? 0 : 1;
            this.pinyinLastQuizDate = SystemClock.uptimeMillis();
        }
    }
}
