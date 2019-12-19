package com.wisdompark.minichoucreme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.login.EmailPasswordActivity;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.util.StringTokenizer;

public class FPrintDetailActivity extends AppCompatActivity {
    private static final String TAG = "FPrintDetailActivity";
    String strFInfo = "";
    String strTime = "";

    TextView txt1;
    TextView txt2;
    TextView txt3;
    TextView txt4;
    TextView txt5;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fprint_detail);

        Intent intent = getIntent();
        String action = intent.getAction();

        strFInfo = intent.getStringExtra("EXTRA_FINFO");
        Log.d(TAG,"JH-strFInfo:"+strFInfo);

        txt1 = (TextView)findViewById(R.id.f_detail_txt1);
        txt2 = (TextView)findViewById(R.id.f_detail_txt2);
        txt3 = (TextView)findViewById(R.id.f_detail_txt3);
        txt4 = (TextView)findViewById(R.id.f_detail_txt4);
        txt5 = (TextView)findViewById(R.id.f_detail_txt5);

        StringTokenizer token = new StringTokenizer(strFInfo,"|");
        Log.d(TAG,"JH-"+token.countTokens());
        txt1.setText(token.nextToken());
        strTime = token.nextToken();
        txt2.setText(strTime); //Key Value
        txt3.setText(token.nextToken());
        txt4.setText(token.nextToken());
        txt5.setText(token.nextToken());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String emailAddress = MiniChouContext.getWatching_email();
        Intent intent;
        switch(id){
            case R.id.menu_detail_remove:
                databaseReference
                        .child(emailAddress)
                        .child(Constraints.FPRINT_NAME)
                        .child(strTime)
                        .removeValue();

                finish();
                break;

            case R.id.menu_detail_modify:
               //String s =  databaseReference.child(emailAddress).child(Constraints.FPRINT_NAME).;
               //Log.d(TAG,"JH-Key ="+s);
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}
