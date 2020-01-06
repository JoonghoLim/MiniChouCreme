package com.wisdompark.minichoucreme.engin;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.util.ArrayList;

public class PlaceChildListener implements ChildEventListener {
    private static final String TAG = "JH-PlaceChildListener";
    private static Context mContext;

    private PlaceChildListener(Context context) {
        this.mContext = context;
    }

    private static PlaceChildListener _instance = null;
    public static PlaceChildListener getInstance(Context context){
        if (_instance == null)
            _instance = new PlaceChildListener(context);

        return _instance;
    }
/*
    private static int MESSAGE_WHAT = 0;
    private static Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == MESSAGE_WHAT){
                Log.d(TAG,"JH-handleMessage-PlaceChildListener");

                Intent intent = new Intent(Constraints.INTENT_DB_UPDATED);
                intent.putExtra("TYPE","PLACE");
                mContext.sendBroadcast(intent);
            }
            return false;
        }
    });

    private void sendDelayMessage(){
        Message msg = handler.obtainMessage(MESSAGE_WHAT);
        handler.sendMessageDelayed(msg, 3000);
    }

    private void removeMessage(){
        handler.removeMessages(MESSAGE_WHAT);
    }

 */

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        PlaceInfo info = dataSnapshot.getValue(PlaceInfo.class);
        Log.d(TAG,"JH-onChildAdded:"+info+ " / s="+s);
        MiniChouContext.getmPlaceInfoList().add(info);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        PlaceInfo info = dataSnapshot.getValue(PlaceInfo.class);
        ArrayList<PlaceInfo> tmpArray = new ArrayList<>();
        Log.d(TAG,"JH-onChildChanged:"+info+" | s:"+s);

        tmpArray = removeElemnets(MiniChouContext.getmPlaceInfoList(),info.getMacList().get(0));
        MiniChouContext.getmPlaceInfoList().clear();
        tmpArray.add(info);
        Log.d(TAG,"JH-"+tmpArray);
        MiniChouContext.setmPlaceInfoList(tmpArray);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        PlaceInfo info = dataSnapshot.getValue(PlaceInfo.class);
        Log.d(TAG,"JH-onChildRemoved"+info);
        String sentKey = MiniChouUtils.getPreference(mContext,Constraints.PREF_SENT_KEY);
        if(sentKey.equals(info.getKey())){ //Sent Key와 동일한 키를 지우면 SentKey도 초기화
            MiniChouUtils.setPreference(mContext,Constraints.PREF_SENT_KEY,Constraints.OUTPLACE_KEY);
        }
        ArrayList<PlaceInfo> pInfoList = removeElemnets(MiniChouContext.getmPlaceInfoList(),info.getKey());
        MiniChouContext.setmPlaceInfoList(pInfoList);
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        PlaceInfo info = dataSnapshot.getValue(PlaceInfo.class);
        Log.d(TAG,"JH-onChildMoved:"+info+" | s:"+s);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(TAG,"JH-onCancelled:"+databaseError);
    }

    private ArrayList<PlaceInfo> removeElemnets(ArrayList<PlaceInfo> list, String rmElement){
        ArrayList<PlaceInfo> rList = new ArrayList<>();

        for(PlaceInfo aInfo: list){
            Log.d(TAG,"JH-aInfo:"+aInfo.getMacList().get(0)+"| rmElement:"+rmElement);

            if(aInfo.getMacList().get(0).equals(rmElement))
                continue;

            rList.add(aInfo);
        }

        return rList;
    }
}
