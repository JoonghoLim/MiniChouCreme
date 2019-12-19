package com.wisdompark.minichoucreme.engin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wisdompark.minichoucreme.storage.CommandInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.util.HashMap;

public class CommandRESPValueEventListener implements ValueEventListener {
    public static boolean isAlreadyRun = false;
    private boolean isFirstCall = true;
    final private static String TAG = "CMD_RESP";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private Context mContext;

    private CommandRESPValueEventListener(Context context) {
        mContext = context;
    }
    private static CommandRESPValueEventListener _instance = null;
    public static CommandRESPValueEventListener getInstance(Context context){
        isAlreadyRun = true;
        if (_instance == null)
            _instance = new CommandRESPValueEventListener(context);

        return _instance;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        CommandInfo value = dataSnapshot.getValue(CommandInfo.class);
        int cmdResType = 0;

        if(value == null)
            return;

        cmdResType = value.getmCMDType();

        Log.d(TAG, "JH-MyUserId:"+MiniChouUtils.getUserId(mContext)+" / CMD_ID:"+value.getmSenderId()+"\n Value is: " + value);

        if(MiniChouUtils.getUserId(mContext).equals(value.getmSenderId()) == false) //내가 보낸것에 대한 수신만
            return;

        PlaceInfo info = value.getObj();
        if(info == null){
                //|| info.getKey()==null || info.getMacList() == null || info.getApList() == null) {
            //Log.d(TAG, "JH-Return Remote getKey():"+info.getKey()+" | getApList():"+info.getApList()+ " | getMacList():"+info.getMacList());
            return;
        }

        Log.d(TAG, "JH-Pass2");

        switch (cmdResType){
            case Constraints.COMMAND_TYPE_REMOTESEARCH:
                Intent intent = new Intent(Constraints.INTENT_REMOTE_RESULTS_RECEIVED);
                intent.putExtra("PLACE_INFO_KEY_TIME",info.getKey());
                intent.putExtra("PLACE_INFO_AP",info.getApList());
                intent.putExtra("PLACE_INFO_MAC",info.getMacList());
                mContext.sendBroadcast(intent);
                break;
            default:
                break;
        }

        Intent intent = new Intent(Constraints.INTENT_DB_UPDATED);
        intent.putExtra("TYPE","CMD_RESP");
        mContext.sendBroadcast(intent);

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
