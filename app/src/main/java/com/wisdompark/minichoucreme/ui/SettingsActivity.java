package com.wisdompark.minichoucreme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.wisdompark.minichoucreme.R;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final String[] LIST_MENU = {"General", "Adding Place"} ;
    ListView menu_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU) ;
        menu_list = (ListView) findViewById(R.id.settings_list);
        menu_list.setAdapter(adapter);
        menu_list.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(this, SettingsGeneralActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
            case 1:
                intent = new Intent(this, SettingsAddPlaceActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}