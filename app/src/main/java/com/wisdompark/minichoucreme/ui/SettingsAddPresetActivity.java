package com.wisdompark.minichoucreme.ui;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.util.ArrayList;

public class SettingsAddPresetActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_input;
    String key;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_add_preset);

        btn_input = (Button) findViewById(R.id.btn_input);
        btn_input.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String emailAddress = MiniChouContext.getWatching_email();
        ArrayList<String > maclist = new ArrayList<>();
        ArrayList<String> aplist = new ArrayList<>();

        key = "등현초-교실앞";
        maclist.add("5a:65:79:0f:27");
        maclist.add("32:cd:a7:32:de:38");
        maclist.add("02:e0:b5:3f:bf:95");
        for(int i = 0; i<maclist.size();i++){
            aplist.add("Pre"+maclist.get(i));
        }
        PlaceInfo pInfo = new PlaceInfo(key,maclist,aplist);
        //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
        databaseReference
                .child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .child(pInfo.getKey())
                .setValue(pInfo);
        maclist.clear();
        aplist.clear();

        key = "등현초-본관";
        maclist.add("32:cd:a7:9c:3b:66");
        maclist.add("20:db:ab:78:a6:6f");
        maclist.add("20:db:ab:78:a6:6e");

        for(int i = 0; i<maclist.size();i++){
            aplist.add("Pre"+maclist.get(i));
        }
        pInfo = new PlaceInfo(key,maclist,aplist);
        //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
        databaseReference
                .child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .child(pInfo.getKey())
                .setValue(pInfo);
        maclist.clear();
        aplist.clear();

        key = "합기도";
        //maclist.add("b4:a9:4f:15:ba:21");
        maclist.add("88:36:6c:4d:73:c4");
        maclist.add("e4:e7:49:d7:95:be");
        for(int i = 0; i<maclist.size();i++){
            aplist.add("Pre"+maclist.get(i));
        }
        pInfo = new PlaceInfo(key,maclist,aplist);
        //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
        databaseReference
                .child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .child(pInfo.getKey())
                .setValue(pInfo);
        maclist.clear();
        aplist.clear();


        key = "등현초 사거리";
        maclist.add("b4:a9:4f:15:ba:21");
        for(int i = 0; i<maclist.size();i++){
            aplist.add("Pre"+maclist.get(i));
        }
        pInfo = new PlaceInfo(key,maclist,aplist);
        //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
        databaseReference
                .child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .child(pInfo.getKey())
                .setValue(pInfo);

        maclist.clear();
        aplist.clear();

        key = "피아노-9단지복지관";
        maclist.add("48:0f:cf:e1:fa:49");
        maclist.add("00:30:0d:92:95:a0");
        maclist.add("ec:8e:b5:05:f4:c4");
        //maclist.add("06:30:0d:92:94:41");
        //maclist.add("06:30:0d:92:94:42");
        //maclist.add("06:30:0d:92:94:46");
        for(int i = 0; i<maclist.size();i++){
            aplist.add("Pre"+maclist.get(i));
        }
        pInfo = new PlaceInfo(key,maclist,aplist);
        //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
        databaseReference
                .child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .child(pInfo.getKey())
                .setValue(pInfo);
        maclist.clear();
        aplist.clear();
    }

    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

}
