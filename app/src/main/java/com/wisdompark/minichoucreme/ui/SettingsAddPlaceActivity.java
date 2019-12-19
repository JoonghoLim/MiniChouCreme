package com.wisdompark.minichoucreme.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.util.ArrayList;

public class SettingsAddPlaceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    private ListViewCustomAdapter adapter;

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
        setContentView(R.layout.activity_settings_add_place);

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
}
