package com.chinese.flashcards.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.chinese.flashcards.services.DataService;
import com.chinese.flashcards.services.QuizService;
import com.chinese.flashcards.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check Chinese Quiz Mode by default
        RadioGroup  quizModes = (RadioGroup)findViewById(R.id.quizmode_radio_group);
        RadioButton chinese   = (RadioButton)findViewById(R.id.radio_chinese);
        quizModes.check(chinese.getId());

        // Set default Cards count to the value set in the Hint
        EditText cardsCount = (EditText)findViewById(R.id.cardscount_edit_text);
        cardsCount.setText(cardsCount.getHint());

        // Start all services used by this app
        startServices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.start_quiz_button);
        fab.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getApplicationContext().startActivity(new Intent(this.getApplicationContext(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Starts all services used by this App
     */
    private void startServices() {
        startService(new Intent(getApplicationContext(), DataService.class));
        startService(new Intent(getApplicationContext(), QuizService.class));
    }

    /**
     * Stops all services used by this App
     */
    private void stopServices() {
        stopService(new Intent(getApplicationContext(), DataService.class));
        stopService(new Intent(getApplicationContext(), QuizService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopServices();
    }

    /**
     * Start Quiz button click event
     * @param v
     */
    @Override
    public void onClick(View v) {
        Bundle quizInfoBundle     = new Bundle();
        // Get Cards Count
        EditText cardsCount = (EditText)findViewById(R.id.cardscount_edit_text);

        // Validate CardsCount
        if (Integer.valueOf(cardsCount.getText().toString()) <= 0) {
            Toast.makeText(getApplicationContext(), "Number of cards must be at least 1", Toast.LENGTH_LONG).show();
            return;
        }

        // Get Checked Quiz Mode
        RadioGroup  quizModes        = (RadioGroup)findViewById(R.id.quizmode_radio_group);
        int         checkedModeIndex = quizModes.getCheckedRadioButtonId();
        RadioButton checkedMode      = (RadioButton)findViewById(checkedModeIndex);

        quizInfoBundle.putString(getResources().getString(R.string.QuizMode), checkedMode.getText().toString());
        quizInfoBundle.putInt(getResources().getString(R.string.CardsCount), Integer.valueOf(cardsCount.getText().toString()));

        Intent quizActivityIntent = new Intent(getApplicationContext(), QuizActivity.class);
        quizActivityIntent.putExtras(quizInfoBundle);
        startActivity(quizActivityIntent);
    }
}
