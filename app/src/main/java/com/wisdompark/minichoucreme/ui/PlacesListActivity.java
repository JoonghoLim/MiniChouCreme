package com.wisdompark.minichoucreme.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.util.ArrayList;

public class PlacesListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    ListView listView;
    private ListViewCustomAdapter adapter;

    private FloatingActionButton fab_main, fab_sub1, fab_sub2,fab_sub3, fab_sub4;
    private Animation fab_open, fab_close;
    private boolean isFabOpen = false;
    private Context mContext;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null & action.equals(Constraints.INTENT_DB_UPDATED)){
                updateUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();
        fab_open = AnimationUtils.loadAnimation(mContext, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(mContext, R.anim.fab_close);
        fab_main = (FloatingActionButton) findViewById(R.id.fab_main);
        fab_sub1 = (FloatingActionButton) findViewById(R.id.fab_sub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fab_sub2);
        fab_sub3 = (FloatingActionButton) findViewById(R.id.fab_sub3);
        fab_sub4 = (FloatingActionButton) findViewById(R.id.fab_sub4);


        fab_main.setOnClickListener(this);
        fab_sub1.setOnClickListener(this);
        fab_sub2.setOnClickListener(this);
        fab_sub3.setOnClickListener(this);
        fab_sub4.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list_place);
        listView.setOnItemClickListener(this);
        updateUI();
    }

    private void updateUI(){
        adapter = new ListViewCustomAdapter(MiniChouContext.getmPlaceInfoList());
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
        registerReceiver(mReceiver,new IntentFilter(Constraints.INTENT_DB_UPDATED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        overridePendingTransition(0, 0);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adding_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch(id){
            case R.id.place_add:
                intent = new Intent(this, SettingsAddVenueActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.place_manual:
                intent = new Intent(this, SettingsAddManualActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.place_remote:
                intent = new Intent(this, SettingsAddRemoteActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.place_preset:
                intent = new Intent(this, SettingsAddPresetActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

 */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<PlaceInfo> list = MiniChouContext.getmPlaceInfoList();
        PlaceInfo placeInfo = list.get(position);

        String key = placeInfo.getKey();
        String apName = placeInfo.getApList().toString();
        String macName = placeInfo.getMacList().toString();

        String strExtra = key+"|"+macName+"|"+apName;
        Intent intent = new Intent(this,PlaceDetailActivity.class);
        intent.putExtra("EXTRA_PLACEINFO",strExtra);
        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.fab_main:
                toggleFab();
                break;

            case R.id.fab_sub1: //Venue Activity
                toggleFab();
                intent = new Intent(this, SettingsAddVenueActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            case R.id.fab_sub2: //Manual Activity
                toggleFab();
                intent = new Intent(this, SettingsAddManualActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            case R.id.fab_sub3: //Remote Activity
                toggleFab();
                intent = new Intent(this, SettingsAddRemoteActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            case R.id.fab_sub4: //Preset
                toggleFab();
                intent = new Intent(this, SettingsAddPresetActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                break;

        }
    }

    private void toggleFab() {

        if (isFabOpen) {
            fab_main.setImageResource(R.drawable.ic_add);
            fab_sub1.startAnimation(fab_close);
            fab_sub2.startAnimation(fab_close);
            fab_sub3.startAnimation(fab_close);
            fab_sub4.startAnimation(fab_close);

            fab_sub1.setClickable(false);
            fab_sub2.setClickable(false);
            fab_sub3.setClickable(false);
            fab_sub4.setClickable(false);
            isFabOpen = false;
        } else {
            fab_main.setImageResource(R.drawable.ic_cancel);
            fab_sub1.startAnimation(fab_open);
            fab_sub2.startAnimation(fab_open);
            fab_sub3.startAnimation(fab_open);
            fab_sub4.startAnimation(fab_open);

            fab_sub1.setClickable(true);
            fab_sub2.setClickable(true);
            fab_sub3.setClickable(true);
            fab_sub4.setClickable(true);
            isFabOpen = true;
        }

    }
}
