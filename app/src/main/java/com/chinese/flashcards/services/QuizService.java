package com.chinese.flashcards.services;

import android.content.Context;

import com.chinese.flashcards.models.ApplicationContext;
import com.chinese.flashcards.models.Card;
import com.chinese.flashcards.models.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class QuizService extends ApplicationContext {

    private final int                    cardsCount;
    private final String                 language;
    private final DataService            dataService;
    private final List<Card>             cards;
    private       Map<Integer, Question> quiz; // <Card ID, Question>

    public QuizService(Context c, int cardsCount, String language) throws IOException {
        super(c);
        this.cardsCount  = cardsCount;
        this.language    = language;
        this.dataService = new DataService(c);
        this.cards       = this.dataService.getCards();
        this.quiz        = new HashMap<Integer, Question>(this.cardsCount);
    }

    public Question getNextQuestion() {

        if (quizDone()) {
            return null;
        }

        // Get Cards
        int           cardIndex  = this.getRandomCard(this.quiz.keySet());
        List<Integer> allChoices = new ArrayList<Integer>();

        while (allChoices.size() < 8) {
            int choiceIndex = this.getRandomCard(allChoices);
            allChoices.add(choiceIndex);

        }
        // Create Question
        Question newQuestion = new Question(this.getContext(),
                                            this.cards.get(cardIndex),
                                            this.language,
                                            allChoices);

        // Remember Question
        this.quiz.put(cardIndex, newQuestion);

        return newQuestion;
    }

    public Card getCard(int index) {
        return this.cards.get(index);
    }

    public boolean quizDone() {
        return (this.cardsCount == this.quiz.size());
    }

    private int getRandomCard(int excludedCard) {
        int randIndex = -1;

        do {
            randIndex = new Random().nextInt(this.cards.size());
        } while (excludedCard == randIndex);

        return randIndex;
    }

    private int getRandomCard(Collection<Integer> excludedCards) {
        int randIndex = -1;

        do {
            randIndex = new Random().nextInt(this.cards.size());
        } while (excludedCards.contains(randIndex));

        return randIndex;
    }
}
