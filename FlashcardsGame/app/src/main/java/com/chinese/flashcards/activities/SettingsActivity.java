package com.chinese.flashcards.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.chinese.flashcards.R;
import com.chinese.flashcards.services.DataService;
import com.chinese.flashcards.services.ServiceConnection;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    private ServiceConnection<DataService> dataServiceConnection;

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (!URLUtil.isValidUrl(stringValue)) {
            Toast.makeText(getApplicationContext(), "Invalid URL", LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(getResources().getString(R.string.DictionaryUrlPreference))) {
             // If the service is already bound, request the update directly on
            // the service
            if (this.dataServiceConnection.isBound()) {
                if (this.dataServiceConnection.getService().update()) {
                    Toast.makeText(getApplicationContext(), "Dictionary updated successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update Dictionary. Resetting Dictionary URL to default.", Toast.LENGTH_LONG).show();

                    GeneralPreferenceFragment preferenceFragment = (GeneralPreferenceFragment)getFragmentManager().findFragmentById(android.R.id.content);
                    EditTextPreference textPreference = (EditTextPreference)preferenceFragment.findPreference(getResources().getString(R.string.DictionaryUrlPreference));
                    textPreference.setText(getResources().getString(R.string.DefaultDictionaryUrl));
                }
            }
            // Otherwise, queue the update on the dataServiceConnection
            else if (this.dataServiceConnection.onServiceConnectedCallback == null)
                this.dataServiceConnection.onServiceConnectedCallback = new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        if (SettingsActivity.this.dataServiceConnection.getService().update()) {
                            Toast.makeText(getApplicationContext(), "Dictionary updated successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to update Dictionary. Resetting Dictionary URL to default.", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                         .registerOnSharedPreferenceChangeListener(this);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,  new GeneralPreferenceFragment())
                .commit();

        this.dataServiceConnection = new ServiceConnection<>();
        getApplicationContext().bindService(new Intent(getApplicationContext(), DataService.class), this.dataServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        GeneralPreferenceFragment preferenceFragment = (GeneralPreferenceFragment)getFragmentManager().findFragmentById(android.R.id.content);
        EditTextPreference textPreference = (EditTextPreference)preferenceFragment.findPreference(getResources().getString(R.string.DictionaryUrlPreference));
        textPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext())
                         .unregisterOnSharedPreferenceChangeListener(this);
        if (this.dataServiceConnection.isBound())
            getApplicationContext().unbindService(this.dataServiceConnection);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                this.getActivity().finish();
                // Go to MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                getActivity().getApplicationContext().startActivity(intent);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
