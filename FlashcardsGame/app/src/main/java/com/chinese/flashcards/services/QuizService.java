package com.chinese.flashcards.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.Card;
import com.chinese.flashcards.models.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuizService extends Service {

    private int                            cardsCount;
    private String                         language;
    private ServiceConnection<DataService> dataServiceConnection;
    private List<Card>                     cards;
    private Map<Integer, Question>         quiz; // <Card ID, Question>
    private long                           quizElapsedTime;

    private final IBinder serviceBinder = new ServiceBinder<DataService>(QuizService.this);

    public void initializeQuiz(int cardsCount, String quizMode) {
        this.cardsCount      = cardsCount;
        this.language        = quizMode;
        this.quizElapsedTime = 0;
        this.quiz.clear();

        // Final attempt to get cards if DataService failed to provide them earlier.
        if ((this.cards == null || this.cards.isEmpty()) && this.dataServiceConnection.getService() != null) {
            try {
               QuizService.this.cards = this.dataServiceConnection.getService().getCards(false);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public Question getNextQuestion() {

        if (quizDone() || this.cards == null || this.cards.isEmpty()) {
            return null;
        }

        // Get Cards
        int           cardIndex  = this.getRandomCard(this.quiz.keySet());
        List<Integer> allChoices = new ArrayList<>();

        // Add correct answer temporarily to avoid choice duplication
        allChoices.add(cardIndex);

        while (allChoices.size() < 8) {
            int choiceIndex = this.getRandomCard(allChoices);
            allChoices.add(choiceIndex);

        }
        allChoices.remove((Integer)cardIndex);

        // Create Question
        Question newQuestion = new Question(getApplicationContext(),
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

    public int getQuestionNumber() {
        return this.quiz.size();
    }

    public Bundle getResults() {
        if (!this.quizDone()) {
            return null;
        }

        int correctCount    = 0;
        int incorrectCount  = 0;
        int knownWordsCount = 0;

        for (Map.Entry<Integer, Question> entry : this.quiz.entrySet()) {

            Question question = entry.getValue();

            boolean qResult = question.getResult();
            correctCount   += (qResult) ? 1 : 0;
            incorrectCount += (qResult) ? 0 : 1;
        }

        for (Card card : this.cards) {
            knownWordsCount += card.isKnownWord() ? 1 : 0;
        }

        // Bundle up all results
        Bundle results = new Bundle();
        results.putInt(getResources().getString(R.string.CorrectCount), correctCount);
        results.putInt(getResources().getString(R.string.WrongCount), incorrectCount);
        results.putInt(getResources().getString(R.string.KnownWordsCount), knownWordsCount);

        return results;
    }

    public void setQuizElapsedTime(long time) {
        this.quizElapsedTime = time;
    }
    public long getQuizElapsedTime() {
        return this.quizElapsedTime;
    }

    private int getRandomCard(Collection<Integer> excludedCards) {
        int randIndex = -1;

        do {
            randIndex = new Random().nextInt(this.cards.size());
        } while ((this.cards.size() > 1) && excludedCards.contains(randIndex));

        return randIndex;
    }

    /**
     * Android Service Implementation
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.dataServiceConnection = new ServiceConnection<>();
        this.quiz                  = new HashMap<Integer, Question>(this.cardsCount);
        this.cards                 = new ArrayList<>();
        getApplicationContext().bindService(new Intent(getApplicationContext(), DataService.class), this.dataServiceConnection, Context.BIND_AUTO_CREATE);

        this.dataServiceConnection.onServiceConnectedCallback = new Handler.Callback() {
            public boolean handleMessage(Message message) {
                try {
                    QuizService.this.cards = QuizService.this.dataServiceConnection.getService().getCards(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        };

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (this.dataServiceConnection.isBound())
            getApplicationContext().unbindService(this.dataServiceConnection);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.cardsCount = intent.getIntExtra(getApplicationContext().getResources().getString(R.string.CardsCount), 0);
        this.language   = intent.getStringExtra(getApplicationContext().getResources().getString(R.string.QuizMode));

        return this.serviceBinder;
    }
}
