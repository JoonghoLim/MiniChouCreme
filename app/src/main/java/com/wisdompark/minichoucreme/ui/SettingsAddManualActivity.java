package com.wisdompark.minichoucreme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.util.ArrayList;

public class SettingsAddManualActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    EditText editPlace;
    EditText editMac1;
    EditText editMac2;
    EditText editMac3;

    Button btnSave;
    Button btnCancel;
    private static String TAG="Mannual Input";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_add_manual);


        editPlace = (EditText)findViewById(R.id.manual_place);
        editMac1 = (EditText) findViewById(R.id.manual_mac1);
        editMac2 = (EditText) findViewById(R.id.manual_mac2);
        editMac3 = (EditText) findViewById(R.id.manual_mac3);

        btnSave = (Button)findViewById(R.id.manual_insert);
        btnCancel = (Button)findViewById(R.id.manual_delete);

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.manual_insert:
                ArrayList<String> arrayMacList = new ArrayList<>();
                ArrayList<String> arrayAPList = new ArrayList<>();
                String place = editPlace.getText().toString();
                String mac1 = editMac1.getText().toString();
                String mac2 = editMac2.getText().toString();
                String mac3 = editMac3.getText().toString();
                if(place.length()==0) break;
                else
                    editPlace.setText("");
                if(mac1.length() !=0) {arrayMacList.add(mac1); arrayAPList.add("Man"+mac1); editMac1.setText("");}
                if(mac2.length() !=0) {arrayMacList.add(mac2); arrayAPList.add("Man"+mac2); editMac2.setText("");}
                if(mac3.length() !=0) {arrayMacList.add(mac3); arrayAPList.add("Man"+mac3); editMac3.setText("");}

                String emailAddress = MiniChouContext.getWatching_email();
                PlaceInfo pInfo = new PlaceInfo(place,arrayMacList,arrayAPList);
                //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
                databaseReference
                        .child(emailAddress)
                        .child(Constraints.PLACE_NAME)
                        .child(pInfo.getKey())
                        .setValue(pInfo);

                Log.d(TAG,"Save Done");


                break;

            case R.id.manual_delete:
                break;
            default:

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
