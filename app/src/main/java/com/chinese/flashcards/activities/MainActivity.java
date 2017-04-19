package com.chinese.flashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chinese.flashcards.R;

public class MainActivity extends AppCompatActivity {

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
        EditText cardsCount   = (EditText)findViewById(R.id.cardscount_edit_text);
        cardsCount.setText(cardsCount.getHint());

        //
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent quizActivityIntent = new Intent(getApplicationContext(), QuizActivity.class);
                Bundle quizInfoBundle     = new Bundle();

                // Get Checked Quiz Mode
                RadioGroup  quizModes        = (RadioGroup)findViewById(R.id.quizmode_radio_group);
                int         checkedModeIndex = quizModes.getCheckedRadioButtonId();
                RadioButton checkedMode      = (RadioButton)findViewById(checkedModeIndex);

                // Get Cards Count
                EditText    cardsCount       = (EditText)findViewById(R.id.cardscount_edit_text);

                quizInfoBundle.putString("Language", checkedMode.getText().toString());
                quizInfoBundle.putInt("CardsCount", Integer.valueOf(cardsCount.getText().toString()));

                quizActivityIntent.putExtras(quizInfoBundle);
                startActivityForResult(quizActivityIntent, 0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           // startActivityForResult(new Intent(this.getApplicationContext(), SettingsActivity.class), 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
