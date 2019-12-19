package com.wisdompark.minichoucreme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.CommandRESPValueEventListener;
import com.wisdompark.minichoucreme.engin.CommandValueEventListener;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.CommandInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.util.ArrayList;

public class SettingsAddRemoteActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "SettingsAddRemoteActivity";
    EditText editPlace;
    Button btnRefresh;
    Button btnInsert;
    Button btnDelete;
    ListView listView;
    TextView txtTitle;
    String strKeyTime = "";
    ArrayList<String> macList = new ArrayList<>();
    ArrayList<String> apList = new ArrayList<>();
    ArrayList<String> info_ap = new ArrayList<>();
    ArrayList<String> info_mac = new ArrayList<>();

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            long time = 0;
            String date = "";
            strKeyTime = intent.getStringExtra("PLACE_INFO_KEY_TIME");
            Log.d(TAG,"JH-strKeyTiem:"+strKeyTime);
            try {
                time = Long.parseLong(strKeyTime);
                date = MiniChouUtils.mills2Date(time, 3);
            }catch(Exception e){
                e.printStackTrace();
                date = "Unknown";
            }
            info_ap = intent.getStringArrayListExtra("PLACE_INFO_AP");
            info_mac = intent.getStringArrayListExtra("PLACE_INFO_MAC");
            if(action != null && action.equals(Constraints.INTENT_REMOTE_RESULTS_RECEIVED)){
                macList = info_mac;
                apList = info_ap;
                txtTitle.setText("Remote Search Complete Updated:"+date);

                updateUI();
            }
        }
    };

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String emailAddress = MiniChouContext.getWatching_email();
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_add_remote);

        editPlace = (EditText) findViewById(R.id.remote_place);
        btnRefresh = (Button) findViewById(R.id.remote_refresh);
        btnInsert = (Button) findViewById(R.id.remote_insert);
        btnDelete = (Button) findViewById(R.id.remote_delete);
        listView = (ListView) findViewById(R.id.remote_ap_list);
        txtTitle = (TextView) findViewById(R.id.remote_txt_title);

        btnRefresh.setOnClickListener(this);
        btnInsert.setOnClickListener(this);
        updateUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,new IntentFilter(Constraints.INTENT_REMOTE_RESULTS_RECEIVED));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.remote_refresh:
                txtTitle.setText("Requesting Remote Search...");
                CommandInfo info = new CommandInfo(MiniChouUtils.getUserId(this)
                        , Constraints.COMMAND_TYPE_REMOTESEARCH
                        , 0
                        , System.currentTimeMillis()
                        , null);

                databaseReference.child(emailAddress)
                        .child(Constraints.COMMAND_SEND_NAME).setValue(info);

                break;
            case R.id.remote_insert:
                String title = editPlace.getText().toString();
                String msg = "";

                if (title.length() == 0) {
                    Toast.makeText(this, "Please 1 more character for title", Toast.LENGTH_SHORT).show();

                }else if(listView.getCheckedItemCount() == 0){
                    Toast.makeText(this,"Please select one more item",Toast.LENGTH_SHORT).show();
                }
                /*
                else if(keys.contains(title)){
                    Toast.makeText(this,"Title have to be unique",Toast.LENGTH_SHORT).show();
                }
                 */
                else {
                    editPlace.setText("");
                    ArrayList<String> checkItems = new ArrayList<>();
                    ArrayList<String> checkAPItems = new ArrayList<>();

                    txtTitle.setText("");
                    for (int i = 0; i < apList.size(); i++) //Only save checked mac address
                    {
                        if (listView.isItemChecked(i) == true) {
                            checkItems.add(macList.get(i));
                            checkAPItems.add(apList.get(i));
                        }
                    }
                    //pMngr.setStringArrayPref(this,title,checkItems);
                    String emailAddress =  MiniChouContext.getWatching_email();
                    PlaceInfo pInfo = new PlaceInfo();
                    pInfo.setKey(title);
                    pInfo.setMacList(checkItems);
                    pInfo.setApList(checkAPItems);
                    //databaseReference.child(emailAddress).child(Constraints.PLACE_NAME).push().setValue(pInfo);
                    databaseReference
                            .child(emailAddress)
                            .child(Constraints.PLACE_NAME)
                            .child(pInfo.getKey())
                            .setValue(pInfo);
                    break;
                }
        }
    }

    private void updateUI(){
        if(info_ap == null)
            return;

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, apList);
        listView.setAdapter(adapter);
        for(int i=0;i<apList.size();i++)
            listView.setItemChecked(i,true);
    }

}
