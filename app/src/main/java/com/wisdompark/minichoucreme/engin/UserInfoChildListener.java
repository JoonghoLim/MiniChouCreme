package com.wisdompark.minichoucreme.engin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.wisdompark.minichoucreme.storage.UserInfo;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.util.ArrayList;

public class UserInfoChildListener implements ChildEventListener {
    private static final String TAG = "JH-UserInfoChildListen";
    private static Context mContext;

    private UserInfoChildListener(Context context) {
        this.mContext = context;
    }

    private static UserInfoChildListener _instance = null;
    public static UserInfoChildListener getInstance(Context context){
        if (_instance == null)
            _instance = new UserInfoChildListener(context);

        return _instance;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        UserInfo info = dataSnapshot.getValue(UserInfo.class);
        Log.d(TAG,"JH-onChildAdded:"+info+ " / s="+s);
        MiniChouContext.getmUserInfoList().add(info);
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        UserInfo info = dataSnapshot.getValue(UserInfo.class);
        Log.d(TAG,"JH-onChildChanged:"+info+" | s:"+s);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        UserInfo info = dataSnapshot.getValue(UserInfo.class);
        Log.d(TAG,"JH-onChildRemoved"+info);
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        UserInfo info = dataSnapshot.getValue(UserInfo.class);
        Log.d(TAG,"JH-onChildMoved:"+info+" | s:"+s);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(TAG,"JH-onCancelled:"+databaseError);
    }

    private ArrayList<UserInfo> removeElemnets(ArrayList<UserInfo> list, String rmElement){
        ArrayList<UserInfo> rList = new ArrayList<>();

        for(UserInfo aInfo: list){
            if(aInfo.getuId().equals(rmElement))
                continue;

            rList.add(aInfo);
        }

        return rList;
    }
}
