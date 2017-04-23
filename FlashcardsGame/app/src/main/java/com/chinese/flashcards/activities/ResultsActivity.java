package com.chinese.flashcards.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chinese.flashcards.R;
import com.chinese.flashcards.plugins.CountUpTimer;
import com.chinese.flashcards.services.QuizService;
import com.chinese.flashcards.services.ServiceConnection;

public class ResultsActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    ServiceConnection<QuizService> quizServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Listen for click events on Home menu item
        BottomNavigationView navigationView = (BottomNavigationView)findViewById(R.id.navigation_menu);
        MenuItem             homeButton     = (MenuItem)navigationView.getMenu().findItem(R.id.navigation_home);
        homeButton.setOnMenuItemClickListener(this);

        // Bind to QuizService
        this.quizServiceConnection = new ServiceConnection<>();
        Intent quizServiceIntent   = new Intent(getApplicationContext(), QuizService.class);
        getApplicationContext().bindService(quizServiceIntent, this.quizServiceConnection, Context.BIND_AUTO_CREATE);
        this.quizServiceConnection.onServiceConnectedCallback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                ResultsActivity.this.displayResults();
                return true;
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.quizServiceConnection.isBound())
            getApplicationContext().unbindService(this.quizServiceConnection);
    }

    private void displayResults() {
        QuizService quizService = this.quizServiceConnection.getService();

        String timeElapsed = CountUpTimer.timeElapsedToString(quizService.getQuizElapsedTime());
        Bundle results     = quizService.getResults();

        if (results == null) {
            Toast.makeText(getApplicationContext(), "Oops. a problem happened while computing the results.", Toast.LENGTH_LONG).show();
        } else {
            // Display results
            TextView timeElapsedView = (TextView)findViewById(R.id.time_elapsed_value);
            TextView cardsCountView  = (TextView)findViewById(R.id.cards_count_value);
            TextView correctAnsView  = (TextView)findViewById(R.id.correct_answers_count);
            TextView wrongAnsView    = (TextView)findViewById(R.id.wrong_answers_count);
            TextView evalMessageView = (TextView)findViewById(R.id.final_evaluation_message);

            // Get values from Bundle
            int correctCount    = results.getInt(getResources().getString(R.string.CorrectCount));
            int wrongCount      = results.getInt(getResources().getString(R.string.WrongCount));
            int knownWordsCount = results.getInt(getResources().getString(R.string.KnownWordsCount));

            // Elapsed Time
            timeElapsedView.setText(timeElapsed);

            // CardsCount
            cardsCountView.setText(String.valueOf(quizService.getQuestionNumber()));

            // Correct and Wrong Answers
            correctAnsView.setText(String.valueOf(correctCount));
            wrongAnsView.setText(String.valueOf(wrongCount));

            // Final Message to user
            evalMessageView.setText(String.format("You have successfully mastered %d word(s).", knownWordsCount));
        }
    }

    /**
     * Home Button
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        finish();

        // Go back to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        getApplicationContext().startActivity(intent);
        return true;
    }
}
