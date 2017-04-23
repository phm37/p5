package com.chinese.flashcards.models;

import android.content.Context;
import android.os.SystemClock;

import com.chinese.flashcards.R;

import java.io.Serializable;
import java.util.Collections;
import java.util.TreeMap;

public class Card extends ApplicationContext {

    public final String chinese;
    public final String pinyin;
    public final String english;

    /**
     * Map<Time, Boolean> to store information about each trials
     */
    TreeMap<Long, Boolean> chineseQuizTries;
    TreeMap<Long, Boolean> pinyinQuizTries;
    TreeMap<Long, Boolean> englishQuizTries;

    public Card(Context c, String chinese, String pinyin, String english) {
        super(c);

        this.chinese          = chinese;
        this.pinyin           = pinyin;
        this.english          = english;
        this.chineseQuizTries = new TreeMap<>(Collections.reverseOrder());
        this.pinyinQuizTries  = new TreeMap<>(Collections.reverseOrder());
        this.englishQuizTries = new TreeMap<>(Collections.reverseOrder());
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
            this.englishQuizTries.put(SystemClock.uptimeMillis(), isSuccess);
        }
        else if (mode.equalsIgnoreCase(chineseMode)) {
            this.chineseQuizTries.put(SystemClock.uptimeMillis(), isSuccess);
        }
        else if (mode.equalsIgnoreCase(pinyinMode)) {
            this.pinyinQuizTries.put(SystemClock.uptimeMillis(), isSuccess);
        }
    }

    /**
     * Checks whether user know the word represented by this card well.
     * The user is considered to know this word if he got 2 parts out of the three right in
     * the past 10 trials.
     */
    public boolean isKnownWord() {

        boolean chineseCorrect = false;
        boolean englishCorrect = false;
        boolean pinyinCorrect  = false;

        // Check whether the user got all three parts correct in the past 10 trials (per Part)
        int trialsCount = 10;
        for (TreeMap.Entry<Long, Boolean> entry : this.chineseQuizTries.entrySet()) {
            if (trialsCount > 0 && entry.getValue()) {
                chineseCorrect = true;
                break;
            }

            trialsCount--;
        }

        trialsCount = 10;
        for (TreeMap.Entry<Long, Boolean> entry : this.englishQuizTries.entrySet()) {
            if (trialsCount > 0 && entry.getValue()) {
                englishCorrect = true;
                break;
            }

            trialsCount--;
        }

        trialsCount = 10;
        for (TreeMap.Entry<Long, Boolean> entry : this.pinyinQuizTries.entrySet()) {
            if (trialsCount > 0 && entry.getValue()) {
                pinyinCorrect = true;
                break;
            }

            trialsCount--;
        }

        return (chineseCorrect || englishCorrect || pinyinCorrect);
    }
}
