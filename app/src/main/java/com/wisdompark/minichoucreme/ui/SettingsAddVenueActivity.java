package com.wisdompark.minichoucreme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SettingsAddVenueActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG ="SettingAddVenueActivity";
    WifiManager wifimanager;
    private List<ScanResult> mScanResult;
    String text = "";
    String result = "";
    private int scanCount;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    //private PlaceFileManager pMngr=null;

    TextView txtTitle;
    TextView txtStatus;
    //TextView txtApList;
    ListView macListView;
    Button btnAdd;
    Button btnDel;
    Button btnRefresh;
    ArrayList<String> mac_list;
    ArrayList<String> ap_list;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                txtStatus.setText("Search Complete.");
                getWIFIScanResult();
                // 몇 초마다 Delay해서 검색을 한다. (milliSec)
                /*
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        wifimanager.startScan();
                    }
                }, 5000);
                 */

            }else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                sendBroadcast(new Intent("wifi.ON_NETWORK_STATE_CHANGED"));
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            Log.d(TAG,"onRequestPermissionsResult()");
            getWIFIScanResult();
        }else{
            Log.d(TAG,"else onRequestPermissionsResult");
        }
    }

    private void getWIFIScanResult() {
        int count=0;
        int idx = 0;
        Log.d(TAG,"Build.VERSION.SDK_INT:"+Build.VERSION.SDK_INT+"\n");
        Log.d(TAG,"Build.VERSION_CODES.M:"+Build.VERSION_CODES.M+"\n");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            Log.d(TAG,"checkSelfPermission()");
        }else{
            Log.d(TAG,"getScanResults()");
            //mScanResult = wifimanager.getScanResults();
            //do something, permission was previously granted; or legacy device
            Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
                @Override
                public int compare(ScanResult lhs, ScanResult rhs) {
                    return (lhs.level > rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
                }
            };
            mScanResult = wifimanager.getScanResults();
            Collections.sort(mScanResult, comparator);
        }

        if(mScanResult == null) return;

        ap_list.clear(); //초기화
        mac_list.clear();

        for(int i=0 ; i<mScanResult.size() && count < 3 ; i++){
            ScanResult result = mScanResult.get(i);

            if(result.SSID.length() == 0) //AP 이름이 없으면 SKIP
                continue;

            count++;

            //ap_list.add(result.SSID+"  ("+result.BSSID+") "+ result.level); //Display
            mac_list.add(result.BSSID);
            ap_list.add(result.SSID); //Display
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, ap_list.toArray()) ;
        macListView.setOnItemSelectedListener(this);
        macListView.setAdapter(adapter);

        for(int i=0;i<count;i++)
            macListView.setItemChecked(i,true);

    }

    public void initWIFIScan(){
        scanCount = 0;
        text = "";
        final IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mReceiver,filter);
        wifimanager.startScan();
        Log.d(TAG,"initWIFIScan()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_add_venue);

        //pMngr = new PlaceFileManager();

        txtTitle = (TextView) findViewById(R.id.place);
        txtStatus = (TextView) findViewById(R.id.txt_title);
        macListView = (ListView) findViewById(R.id.mac_list);

        btnAdd = (Button)findViewById(R.id.insert);
        btnAdd.setOnClickListener(this);
        btnDel = (Button)findViewById(R.id.delete);
        btnDel.setOnClickListener(this);
        btnRefresh = (Button)findViewById(R.id.refresh);
        btnRefresh.setOnClickListener(this);

        wifimanager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        mac_list = new ArrayList<String>();
        ap_list = new ArrayList<String>();
        Log.d(TAG,"Setup WIFIManager getSystemService");

        if(wifimanager.isWifiEnabled() == false)
            wifimanager.setWifiEnabled(true);

        initWIFIScan();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.insert:
                String title = txtTitle.getText().toString();
                String msg = "";
                //ArrayList<String> keys = pMngr.getKeyList(this);

                if(title.length() == 0){
                    Toast.makeText(this,"Please 1 more character for title",Toast.LENGTH_SHORT).show();

                }
                /*
                else if(keys.contains(title)){
                    Toast.makeText(this,"Title have to be unique",Toast.LENGTH_SHORT).show();
                }
                 */
                else {
                    ArrayList<String> checkItems = new ArrayList<>();
                    ArrayList<String> checkAPItems = new ArrayList<>();

                    txtTitle.setText("");
                    for(int i=0;i<mac_list.size();i++) //Only save checked mac address
                    {
                        if(macListView.isItemChecked(i) == true) {
                            checkItems.add(mac_list.get(i));
                            checkAPItems.add(ap_list.get(i));
                        }
                    }
                    //pMngr.setStringArrayPref(this,title,checkItems);
                    String emailAddress = MiniChouContext.getWatching_email();
                    PlaceInfo info = new PlaceInfo();
                    info.setKey(title);
                    info.setMacList(checkItems);
                    info.setApList(checkAPItems);
                    //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(info);
                    databaseReference
                            .child(emailAddress)
                            .child(Constraints.PLACE_NAME)
                            .child(info.getKey())
                            .setValue(info);
                }
                break;

            case R.id.delete:
                //pMngr.removeAll(this);
                break;

            case R.id.refresh:
                wifimanager.startScan();
                txtStatus.setText("Searching......");
                break;

            default:
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
