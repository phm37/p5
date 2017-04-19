package com.chinese.flashcards.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.Question;
import com.chinese.flashcards.plugins.CountUpTimer;
import com.chinese.flashcards.services.QuizService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class QuizActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private int           cardsCount;
    private String        quizLanguage;
    private CountUpTimer  quizTimer;
    private QuizService   quizService;

//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
//            = new BottomNavigationView.OnNavigationItemSelectedListener() {
//
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    mTextMessage.setText(R.string.title_home);
//                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
//                case R.id.navigation_notifications:
//                    mTextMessage.setText(R.string.title_notifications);
//                    return true;
//            }
//            return false;
//        }
//
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Get information sent by MainActivity
        Bundle quizInfoBundle = getIntent().getExtras();
        this.cardsCount       = quizInfoBundle.getInt("CardsCount");
        this.quizLanguage     = quizInfoBundle.getString("Language");

        // Setup QuizService
        try {
            this.quizService = new QuizService(getApplicationContext(), this.cardsCount, this.quizLanguage);
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(), "Oops. Failed to generate Quiz questions!", Toast.LENGTH_SHORT).show();
        }

        // Start Timer (update every 1 ms)
        this.quizTimer = new CountUpTimer(1) {
            @Override
            public void onTick(int milliSeconds) {
                int time    = milliSeconds / 1000;
                int seconds = time % 60;
                time /= 60;
                int minutes = time % 60;
                time /= 60;
                int hours   = time % 24;

                TextView timerView  = (TextView)findViewById(R.id.timer_text_view);
                timerView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }
        };

        // Show first Question. Then, start timer.
        this.showNextQuestion();
        this.quizTimer.start();
    }

    private void showNextQuestion() {
        Question question = this.quizService.getNextQuestion();

        // Get Language Modes from resources
        String englishMode = this.getResources().getString(R.string.EnglishMode);
        String chineseMode = this.getResources().getString(R.string.ChineseMode);
        String pinyinMode  = this.getResources().getString(R.string.PinyinMode);

        // Get View components
        TextView questionView = (TextView)findViewById(R.id.question_text);

        // Randomize choices

        if (this.quizLanguage.equalsIgnoreCase(englishMode)) {
            questionView.setText(question.card.english);

        }

        else if (this.quizLanguage.equalsIgnoreCase(chineseMode)) {
            questionView.setText(question.card.chinese);
        }

        else if (this.quizLanguage.equalsIgnoreCase(pinyinMode)) {
            questionView.setText(question.card.pinyin);
        }

        getFragmentManager().beginTransaction();
    }

}
