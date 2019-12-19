package com.wisdompark.minichoucreme.engin;

import android.app.Notification;
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
import com.wisdompark.minichoucreme.ui.MiniChouNotification;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class FPrintChildListener implements ChildEventListener {

    static Context mContext;
    private static MiniChouNotification mNotification;
    private static final String TAG = "FPrintChildListener";
    private static String notiTitle="";
    private static String notiContent="";
    private static String notiSender = "";
    private static boolean isFirstLoadingCompleted = false;

    private FPrintChildListener(Context context) {
        this.mContext = context;
        mNotification = new MiniChouNotification(context);
        Log.d(TAG,"JH-FPrintChildListener(context)");
        isFirstLoadingCompleted = false;
    }
    private static FPrintChildListener _instance = null;
    public static FPrintChildListener getInstance(Context context){
        isFirstLoadingCompleted = false;
        if (_instance == null)
            _instance = new FPrintChildListener(context);

        return _instance;
    }

    private static int MESSAGE_WHAT = 0;
    private static Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == MESSAGE_WHAT){
                Log.d(TAG,"JH-handleMessage");
                if(isFirstLoadingCompleted != false //처음 대량 로딩 시 Noti 안함
                        && MiniChouContext.getmUserID().equals(notiSender) == false) { //자기가 보낸 메시지는 Noti안함
                    mNotification.showNotification(notiTitle, notiContent);
                }
                isFirstLoadingCompleted = true;
                Intent intent = new Intent(Constraints.INTENT_DB_UPDATED);
                intent.putExtra("TYPE", "FPRINT");
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

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //FPrintInfo fInfo = dataSnapshot.getValue(FPrintInfo.class);
        String strFInfo = dataSnapshot.getValue(String.class);
        StringTokenizer token = new StringTokenizer(strFInfo,"|");

        Log.d(TAG,"JH-strFinfo:"+strFInfo+" / token count:"+token.countTokens()+" / s="+s);
        FPrintInfo fInfo = new FPrintInfo();
        PlaceInfo placeInfo = new PlaceInfo();
        ArrayList<String> pMac = new ArrayList<>();
        ArrayList<String> pAP = new ArrayList<>();


        String tmp = "";

        tmp = token.nextToken();
        Log.d(TAG,"tmp0="+tmp);
        fInfo.setmSenderID(tmp!=null?tmp:"No Value");

        tmp = token.nextToken();
        Log.d(TAG,"tmp1="+tmp);
        fInfo.setmTime(tmp!=null?Long.parseLong(tmp):0);

        tmp = token.nextToken();
        Log.d(TAG,"tmp2="+tmp);
        placeInfo.setKey(tmp!=null?tmp:"No Value");

        tmp = token.nextToken();
        Log.d(TAG,"tmp3="+tmp);
        pMac.add(tmp!=null?tmp:"No Value");
        placeInfo.setMacList(pMac);

        tmp = token.nextToken();
        Log.d(TAG,"tmp4="+tmp);
        pAP.add(tmp!=null?tmp:"No Value");
        placeInfo.setApList(pAP);

        fInfo.setmPlaceInfo(placeInfo);
        MiniChouContext.getmFPrintInfoList().add(fInfo);

        if(isFirstLoadingCompleted == true) {
            Intent intent = new Intent(Constraints.INTENT_DB_UPDATED);
            intent.putExtra("TYPE", "FPRINT");
            mContext.sendBroadcast(intent);
        }

        //if(MiniChouContext.getmUserID().equals(fInfo.getmSenderID())) //자기가 보낸 메시지는 Noti안함
        //    return;

        String place = fInfo.getmPlaceInfo().getKey();
        String mac = fInfo.getmPlaceInfo().getMacList().get(0);
        long time = fInfo.getmTime();

        String verb = "";

        if(mac.equals(Constraints.OUTPLACE_MAC)){
            //Leaving case
            notiTitle = Constraints.STR_OUT_PLACE;

        }else{
            //Enter case
            verb = " 주변에 있습니다";
            notiTitle = "' "+place +" '"+verb;
        }

        notiContent = MiniChouUtils.mills2Date(time,0);
        notiSender = fInfo.getmSenderID();

        if(handler.hasMessages(MESSAGE_WHAT))
            removeMessage();

        sendDelayMessage();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        String strFInfo = dataSnapshot.getValue(String.class);
        Log.d(TAG,"JH-onChildChanged:"+strFInfo+" / s="+s);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        String strFInfo = dataSnapshot.getValue(String.class);
        String strTime = "";
        Log.d(TAG,"JH-onChildRemoved:"+strFInfo);
        StringTokenizer token = new StringTokenizer(strFInfo,"|");
        token.nextToken();
        strTime = token.nextToken();

        ArrayList<FPrintInfo> refreshedFPrintInfo = removeElemnets(MiniChouContext.getmFPrintInfoList(),strTime);
        MiniChouContext.setmFPrintInfoList(refreshedFPrintInfo);

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        String strFInfo = dataSnapshot.getValue(String.class);
        Log.d(TAG,"JH-onChildMoved:"+strFInfo+" / s="+s);

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Log.d(TAG,"JH-onCancelled:"+databaseError);
    }

    private ArrayList<FPrintInfo> removeElemnets(ArrayList<FPrintInfo> flist, String rmElement){
        ArrayList<FPrintInfo> rList = new ArrayList<>();
        long rmTime = Long.parseLong(rmElement);

        for(FPrintInfo aInfo: flist){
            if(aInfo.getmTime() == rmTime)
                continue;

            rList.add(aInfo);
        }

        return rList;
    }
}
