package com.wisdompark.minichoucreme.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.CommandRESPValueEventListener;
import com.wisdompark.minichoucreme.engin.CommandValueEventListener;
import com.wisdompark.minichoucreme.engin.FPrintChildListener;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.engin.PlaceChildListener;
import com.wisdompark.minichoucreme.engin.UserInfoChildListener;
import com.wisdompark.minichoucreme.engin.WifiWorker;
import com.wisdompark.minichoucreme.login.EmailPasswordActivity;
import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list_fprint;
    private MessageAdapter adapter;

    public static final String MESSAGE_STATUS = "MESSAGE_STATUS";
    private static final String TAG = "MainActivity" ;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Intent foregroundServiceIntent = null;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != null & action.equals(Constraints.INTENT_DB_UPDATED)){
                Log.d(TAG,"JH-INTENT_DB_UPDATED");
                updateUI();
            }
        }
    };

    private final int REQUEST_CODE = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.d(TAG,"JH-MainActivity-OnCreate");

        list_fprint = (ListView) findViewById(R.id.list_fprint);
        list_fprint.setOnItemClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        updateUI();


/*
        final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(WifiWorker.class)
                .setInitialDelay(3,TimeUnit.SECONDS) //waiting for DB loading
                .build();

        MiniChouContext.setmUserID(MiniChouUtils.getSenderID(this));


        resettingDB();
*/
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkPermissions()) {
                finish();
            }
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                updateUI();
            }
        };

        //updateUI();
        /*

        mWorkManager.cancelAllWork();
        mWorkManager.enqueue(mRequest);

        setPreferences();
        updateUI();

        Log.d(TAG,"JH-MyEmail:"+MiniChouContext.getMyEmail());
        Log.d(TAG,"JH-isIsParentsMode:"+MiniChouContext.isIsParentsMode());
        Log.d(TAG,"JH-Watching Email:"+MiniChouContext.getWatching_email());
        */
    }

    private void setPreferences() {
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchSP = sp.getBoolean("sync",false);
        String strEmail = sp.getString("watching_email","");

        MiniChouContext.setIsParentsMode(switchSP);
        MiniChouContext.setWatching_email(strEmail);

        if( user != null )
            MiniChouContext.setMyEmail(user.getEmail());
    }

    private void updateUI(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            adapter = new MessageAdapter(this, MiniChouContext.getmFPrintInfoList());
            list_fprint.setAdapter(adapter);
            list_fprint.setSelection(MiniChouContext.getmFPrintInfoList().size() - 1);
        }else{
            finish();
        }
    }
    /* Location permission 을 위한 메서드들 */
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(MainActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("permission", "granted");
                }
            }
        }
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
        Intent intent;
        switch(id){
            /*
            case R.id.action_logout:
                mAuth.signOut();
                finish();
                break;
             */
            case R.id.action_account:
                intent = new Intent(this, EmailPasswordActivity.class);
                intent.putExtra("DISPLAY_TYPE",1); //0: 처음 로그인 확인 //1: Option에서 보여주기
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            case R.id.action_settings:
                intent = new Intent(this, SettingsGeneralActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //startActivity(intent);
                startActivityForResult(intent,REQUEST_CODE);
                break;

            case R.id.action_places:
                intent = new Intent(this, PlacesListActivity.class);
                intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extraBundle;
        //MainActivity에서 부여한 번호표를 비교
        if (requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Log.d(TAG,"JH-onActivityResult - RESULT_OK");
                stopService(foregroundServiceIntent);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        setPreferences();

        if(MiniChouContext.getmPlaceInfoList().size() == 0){
            loadDB();
        }

        final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(WifiWorker.class)
                .setInitialDelay(3, TimeUnit.SECONDS) //waiting for DB loading
                .build();

        mWorkManager.cancelAllWork();
        mWorkManager.enqueue(mRequest);
/*
        if(MiniChouContext.getMyEmail().length() > 0){
            if (null == MiniChouService.serviceIntent) {
                foregroundServiceIntent = new Intent(this, MiniChouService.class);
                startService(foregroundServiceIntent);
                Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();
            } else {
                foregroundServiceIntent = MiniChouService.serviceIntent;
                Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Please Login to Start Service", Toast.LENGTH_LONG).show();
        }
*/
        updateUI();
        registerReceiver(mReceiver,new IntentFilter(Constraints.INTENT_DB_UPDATED));

        Log.d(TAG,"JH-MyEmail:"+MiniChouContext.getMyEmail());
        Log.d(TAG,"JH-isIsParentsMode:"+MiniChouContext.isIsParentsMode());
        Log.d(TAG,"JH-Watching Email:"+MiniChouContext.getWatching_email());
    }

    private void loadDB() {
        settingDB();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearDB();
        unregisterReceiver(mReceiver);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }*/
    }

    private void settingDB(){
        String emailAddress = MiniChouContext.getWatching_email();

        databaseReference.child(emailAddress)
                .child(Constraints.FPRINT_NAME)
                .addChildEventListener( FPrintChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .addChildEventListener( PlaceChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.USER_NAME)
                .addChildEventListener(UserInfoChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_SEND_NAME)
                .addValueEventListener( CommandValueEventListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_RESPONSE_NAME)
                .addValueEventListener( CommandRESPValueEventListener.getInstance(this));
    }

    private void resettingDB() {
        if(MiniChouContext.getMyEmail().length() > 0
                && CommandRESPValueEventListener.isAlreadyRun == false) { //리스너들을 대표해서 하나의 Flag만 확인
            //clearDB();
            settingDB();
        }
    }

        private void clearDB() {
        String emailAddress = MiniChouContext.getWatching_email();

        databaseReference.child(emailAddress)
                .child(Constraints.FPRINT_NAME).removeEventListener(FPrintChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .removeEventListener( PlaceChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                    .child(Constraints.USER_NAME)
                    .removeEventListener( UserInfoChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_SEND_NAME)
                .removeEventListener( CommandValueEventListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_RESPONSE_NAME)
                .removeEventListener( CommandRESPValueEventListener.getInstance(this));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<FPrintInfo> list = MiniChouContext.getmFPrintInfoList();
        FPrintInfo fInfo = list.get(position);
        PlaceInfo placeInfo = fInfo.getmPlaceInfo();
        String key = placeInfo.getKey();
        String apName = placeInfo.getApList().get(0);
        String macName = placeInfo.getMacList().get(0);

        String strExtra = fInfo.toString();
        Log.d(TAG,"JH-strExtra:"+strExtra);
        Intent intent = new Intent(this,FPrintDetailActivity.class);
        intent.putExtra("EXTRA_FINFO",strExtra);
        intent.addFlags (Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

/*
        String message = "Place Name = "+key+"\nAPName = "+apName+"\nMacAddr = "+macName;

        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
        alert.setTitle("AP Info");
        alert.setMessage(message);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
 */
    }

}
