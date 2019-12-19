package com.wisdompark.minichoucreme.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import android.content.Intent;
import android.os.Bundle;

import com.wisdompark.minichoucreme.R;

public class SettingsGeneralActivity extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_general);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsGeneralActivity.SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        intent = new Intent();
        this.setResult(RESULT_OK, intent);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
