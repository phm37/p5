package com.chinese.flashcards.models;

import android.content.Context;

import com.chinese.flashcards.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Question extends ApplicationContext implements Serializable {
    public final Card                    card;
    public final String                  language;
    public final List<Integer>           choices;
    public       Map<String, String>     response; // Map<Language, Answer>

    private Boolean isCorrect;

    public Question(Context c, Card card, String language, List<Integer> choices) {
        super(c);
        this.card      = card;
        this.language  = language;
        this.choices   = choices;
        this.response  = new HashMap<String, String>();
        this.isCorrect = null;
    }

    public boolean getResult() {

        // Handle Question Not answered
        if (this.response.size() != 2) {
            return false;
        }

        // Avoid re-computing result
        if (this.isCorrect != null) {
            return this.isCorrect;
        }

        // Get Language Modes from resources
        String englishMode = this.getContext().getResources().getString(R.string.EnglishMode);
        String chineseMode = this.getContext().getResources().getString(R.string.ChineseMode);
        String pinyinMode  = this.getContext().getResources().getString(R.string.PinyinMode);

        // English Question
        if (englishMode.equalsIgnoreCase(this.language)) {
            // Check Chinese & Pinyin
            if (!this.response.get(chineseMode).equalsIgnoreCase(card.chinese) ||
                !this.response.get(pinyinMode).equalsIgnoreCase(card.pinyin)) {
                this.isCorrect = false;
            }
        }

        // Chinese Question
        else if (chineseMode.equalsIgnoreCase(this.language)) {
            // Check English & Pinyin
            if (!this.response.get(englishMode).equalsIgnoreCase(card.english) ||
                !this.response.get(pinyinMode).equalsIgnoreCase(card.pinyin)) {
                this.isCorrect = false;
            }
        }

        // Pinyin Question
        else if (pinyinMode.equalsIgnoreCase(this.language)) {
            // Check English & Chinese
            if (!this.response.get(englishMode).equalsIgnoreCase(card.english) ||
                !this.response.get(chineseMode).equalsIgnoreCase(card.chinese)) {
                this.isCorrect = false;
            }
        }

        return this.isCorrect;
    }
}
