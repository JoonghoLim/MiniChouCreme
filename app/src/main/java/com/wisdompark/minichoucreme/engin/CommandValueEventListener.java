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

public class CommandValueEventListener implements ValueEventListener {
    final private static String TAG = "CMD_SENDER";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private Context mContext;
    private boolean isFirstCall = true;

    private CommandValueEventListener(Context context) {
        mContext = context;
    }

    private static CommandValueEventListener _instance = null;
    public static CommandValueEventListener getInstance(Context context){
        if (_instance == null)
            _instance = new CommandValueEventListener(context);

        return _instance;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        CommandInfo value = dataSnapshot.getValue(CommandInfo.class);

        if(value == null || MiniChouContext.isIsParentsMode() == true) //자식것만 검색가능 //부모 단말은 패스
            return;

        Log.d(TAG, "JH-MyUserId:"+MiniChouUtils.getUserId(mContext)+" / CMD_ID:"+value.getmSenderId()+"\n Value is: " + value);

        if(MiniChouUtils.getUserId(mContext).equals(value.getmSenderId())) //내가 보낸거면 무시
            return;

        Log.d(TAG, "JH-Pass1");

        switch(value.getmCMDType()){
            case Constraints.COMMAND_TYPE_REMOTESEARCH:
                PlaceInfo pInfo = MiniChouContext.getmRealTimeSearchInfo();
                Log.d(TAG,"JH-COMMAND_TYPE_REMOTESEARCH pInfo:"+pInfo);
                CommandInfo info = new CommandInfo(
                        value.getmSenderId()
                        ,value.getmCMDType()
                        , value.getmCMDNum()+1
                        ,System.currentTimeMillis()
                        ,pInfo);

                String emailAddress =  MiniChouContext.getWatching_email();;

                databaseReference.child(emailAddress)
                        .child(Constraints.COMMAND_RESPONSE_NAME).setValue(info);

                break;
            default:
                break;
        }

        Intent intent = new Intent(Constraints.INTENT_DB_UPDATED);
        intent.putExtra("TYPE","CMD_SENDER");
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
