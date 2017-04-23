package com.chinese.flashcards.activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.Question;
import com.chinese.flashcards.plugins.CountUpTimer;
import com.chinese.flashcards.services.QuizService;
import com.chinese.flashcards.services.ServiceConnection;

public class QuizActivity extends AppCompatActivity implements QuestionFragment.OnFragmentInteractionListener {

    private CountUpTimer quizTimer;
    private ServiceConnection<QuizService> quizServiceConnection;

    private Question currentQuestion = null;
    private Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Bind to QuizService
        this.quizServiceConnection = new ServiceConnection<>();
        Intent quizServiceIntent   = new Intent(getApplicationContext(), QuizService.class);
        getApplicationContext().bindService(quizServiceIntent, this.quizServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Show next question when the next_question_button is clicked
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.next_question_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If Quiz is done, show Results Activity
                if (QuizActivity.this.quizServiceConnection.getService().quizDone()) {
                    QuizActivity.this.quizTimer.stop();
                    QuizActivity.this.quizServiceConnection.getService().setQuizElapsedTime(QuizActivity.this.quizTimer.getElapsed());
                    startActivity(new Intent(QuizActivity.this.getApplicationContext(), ResultsActivity.class));
                    finish();
                }
                else {
                    QuizActivity.this.showNextQuestion();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Setup Timer (update every 1 s)
        this.quizTimer = new CountUpTimer(getApplicationContext(), 1000);
        this.quizTimer.onTickCallback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                Bundle timeBundle = msg.getData();
                TextView timerView = (TextView) findViewById(R.id.timer_text_view);
                timerView.setText(CountUpTimer.timeElapsedToClockString(timeBundle.getInt(getResources().getString(R.string.TimeElapsed))));
                return true;
            }
        };

        // Show first Question. Then, start timer.
        this.quizServiceConnection.onServiceConnectedCallback = new Handler.Callback() {
            public boolean handleMessage(Message msg) {
                QuizActivity.this.quizServiceConnection
                            .getService()
                            .initializeQuiz(getIntent().getIntExtra(getResources().getString(R.string.CardsCount), 0),
                                            getIntent().getStringExtra(getResources().getString(R.string.QuizMode)));
                QuizActivity.this.showNextQuestion();
                QuizActivity.this.quizTimer.start();
                return true;
            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

         // Force Portrait Orientation if the Quiz already started with it (This fixes a visual nuance
         // during rotation of layout
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    @Override
    public void onBackPressed() {
        // Disable Back Button during Quiz so that user
        // cannot change the answer.
    }

    private void showNextQuestion() {
        QuizService quizService = this.quizServiceConnection.getService();
        this.currentQuestion    = quizService.getNextQuestion();

        if (this.currentQuestion == null &&
            !quizService.quizDone()) {
            // Failed to get Question, so show error
            Toast.makeText(getApplicationContext(), "Failed to generate Questions. Please modify URL in Settings to re-download dictionary.", Toast.LENGTH_LONG).show();

            // Go back to MainActivity
            this.finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            getApplicationContext().startActivity(intent);
            return;
        }

        // Disable next_question_button
        FloatingActionButton nextQuestionButton = (FloatingActionButton)findViewById(R.id.next_question_button);
        nextQuestionButton.setVisibility(View.INVISIBLE);

        Intent fragmentIntent = new Intent();
        this.currentFragment = QuestionFragment.newInstance(fragmentIntent);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.question_fragment_container, this.currentFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        // Keep first Question Fragment as root
        if (quizService.getQuestionNumber() > 1)
            ft.addToBackStack(null);
        ft.commit();
    }

    public Question getCurrentQuestion() {
        return this.currentQuestion;
    }

    public ServiceConnection<QuizService> getServiceConnection() {
        return this.quizServiceConnection;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (this.quizServiceConnection.isBound())
            getApplicationContext().unbindService(this.quizServiceConnection);
    }
}
