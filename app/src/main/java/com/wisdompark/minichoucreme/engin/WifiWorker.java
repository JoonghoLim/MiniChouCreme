package com.wisdompark.minichoucreme.engin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.ui.MainActivity;
import com.wisdompark.minichoucreme.ui.MiniChouNotification;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.WIFI_SERVICE;

public class WifiWorker extends Worker {
    private static final String WORK_RESULT = "work_result";
    private static final String TAG = "WifiWorker";
    private WifiManager wifimanager = wifimanager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);;
    private Context mContext;
    private MiniChouNotification mNotification;
    private Intent foregroundServiceIntent = null;

    public WifiWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mNotification = new MiniChouNotification(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean wasWifiSettingEnabled = wifimanager.isWifiEnabled();
        Data taskData = getInputData();
        int currentState = Constraints.MNC_STATE_UNKOWN;
        String taskDataString = taskData.getString(MainActivity.MESSAGE_STATUS);
        Data outputData = new Data.Builder().putString(WORK_RESULT, "Jobs Finished").build();

        preExecution(); //Check and Launch service

        Log.d(TAG,"JH-doWork()");
        if(wasWifiSettingEnabled == false)
            wifimanager.setWifiEnabled(true);

        if(MiniChouContext.isIsParentsMode() != true) {//자녀모드 일 경우
            currentState = getCurrentState();
            doingCase(currentState);
        }

        if(wasWifiSettingEnabled == false)
            wifimanager.setWifiEnabled(wasWifiSettingEnabled); //recover previous value

        postExecution(); //Re-insert Work for Loop periodic
        return Result.success(outputData);
    }

    private void doingCase(int currentState) {
        int lastState = MiniChouContext.getmLastState();


        switch (currentState){
            case Constraints.MNC_STATE_IN_PLACE:
                if(lastState != currentState){
                    //State Changed - OutPlace -> InPlace
                    //********등록 & 알림
                    //String sentPlaceKey = MiniChouContext.getmLastSentPlaceInfo().getKey();
                    String sentPlaceKey = MiniChouUtils.getPreference(mContext,Constraints.PREF_SENT_KEY);
                    String currenPlaceKey = MiniChouContext.getmCurrentPlaceInfo().getKey();
                    Log.d(TAG,"JH-OUT->IN Place -sentPlaceKey:"+sentPlaceKey+" | currenPlaceKey:"+currenPlaceKey);

                    if(sentPlaceKey == null || sentPlaceKey.equals(currenPlaceKey) ==  false){ //'집->OutPlace->집' 인경우 No추가
                        //********등록 & 알림
                        addDBFprint(MiniChouContext.getmCurrentPlaceInfo());
                    }else{ //계속 같은 위치에 머물때
                        //Do nothing
                    }
                }else{
                    //Keep In Place - Inplace -> InPlace
                    //Check whether or not the Place is same
                    //String sentPlaceKey = MiniChouContext.getmLastSentPlaceInfo().getKey();
                    String sentPlaceKey = MiniChouUtils.getPreference(mContext,Constraints.PREF_SENT_KEY);
                    String currenPlaceKey = MiniChouContext.getmCurrentPlaceInfo().getKey();
                    Log.d(TAG,"JH-IN->IN Place sentPlaceKey-Else:"+sentPlaceKey+" | currenPlaceKey:"+currenPlaceKey);

                    if(sentPlaceKey == null || sentPlaceKey.equals(currenPlaceKey) ==  false){
                        //********등록 & 알림
                        addDBFprint(MiniChouContext.getmCurrentPlaceInfo());
                    }else{ //계속 같은 위치에 머물때
                        //Do nothing
                    }
                }
                break;
            case Constraints.MNC_STATE_OUT_OF_PLACE:
                if(lastState != currentState){
                    //addDBFprint(MiniChouContext.getmCurrentPlaceInfo());
                    //State Changed - In Place -> Out Place
                    MiniChouContext.setFirstLeavingTime(System.currentTimeMillis()); //Leaving interval check

                    ArrayList<FPrintInfo> fInfoList = MiniChouContext.getmFPrintInfoList();
                    int lastIdx = fInfoList.size()-1;
                    long lastFPrintTime = 0;

                    String sentPlaceKey = MiniChouUtils.getPreference(mContext,Constraints.PREF_SENT_KEY);
                    String currenPlaceKey = MiniChouContext.getmCurrentPlaceInfo().getKey();
                    Log.d(TAG,"JH-IN->OUT Place sentPlaceKey:"+sentPlaceKey+" | currenPlaceKey:"+currenPlaceKey);

                    if(lastIdx >= 0) {
                        lastFPrintTime = fInfoList.get(lastIdx).getmTime();
                        if(isStayingOverInterval(lastFPrintTime , System.currentTimeMillis()) == true){
                            //등록 & 떠남 알림
                            addDBFprint(MiniChouContext.getmCurrentPlaceInfo());
                        }
                    }
                }else{
                    //Keep Out Place - OutPlace -> Out Place
                    //Do nothing
                    long firstLeavingTime = MiniChouContext.getFirstLeavingTime();
                    long leavingIntervalMins = (System.currentTimeMillis() - firstLeavingTime)/(1000*60);

                    Log.d(TAG,"JH-Out Interval :"+leavingIntervalMins);
                    if(firstLeavingTime != 0
                            && leavingIntervalMins >= Constraints.STAYING_INTERVAL_MIN){
                        // If leaving time is over threshold, then reset LastSendt to get Notification
                        MiniChouContext.setmLastSentPlaceInfo(MiniChouUtils.getFakeOutPlace());
                        MiniChouUtils.setPreference(mContext,Constraints.PREF_SENT_KEY,Constraints.OUTPLACE_KEY);
                    }
                    String sentPlaceKey = MiniChouUtils.getPreference(mContext,Constraints.PREF_SENT_KEY);
                    String currenPlaceKey = MiniChouContext.getmCurrentPlaceInfo().getKey();
                    Log.d(TAG,"JH-OUT->OUT Place sentPlaceKey:"+sentPlaceKey+" | currenPlaceKey:"+currenPlaceKey);
                }
                break;
            default:
                Log.d(TAG,"JH-Error");
                break;
        }

    }

    private void addDBFprint(PlaceInfo getmCurrentPlaceInfo) {
        String emailAddress = MiniChouContext.getWatching_email();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();

        FPrintInfo fPrintInfo = new FPrintInfo(MiniChouContext.getmUserID()
                                                ,System.currentTimeMillis()
                                                ,getmCurrentPlaceInfo);

        Log.d(TAG,"JH-addDBFprint fPrintInfo:"+fPrintInfo.toString());

        //if(MiniChouContext.getmLastState() != Constraints.MNC_STATE_UNKOWN) {//first situation
        String lastSentKey = MiniChouUtils.getPreference(mContext,Constraints.PREF_SENT_KEY);
        if(lastSentKey == null || getmCurrentPlaceInfo.getKey().equals(lastSentKey) == false){
            Log.d(TAG,"JH-addDBFprint SENT?");
            //databaseReference.child(emailAddress).child(Constraints.FPRINT_NAME).push().setValue(fPrintInfo);
            databaseReference
                    .child(emailAddress)
                    .child(Constraints.FPRINT_NAME)
                    .child(""+fPrintInfo.getmTime())
                    .setValue(fPrintInfo.toString());
        }

        MiniChouContext.setmLastSentPlaceInfo(getmCurrentPlaceInfo);
        MiniChouUtils.setPreference(mContext, Constraints.PREF_SENT_KEY,getmCurrentPlaceInfo.getKey()); //초기 부팅 시 확인
    }

    private boolean isStayingOverInterval(long lastFPrintTime, long currentTimeMillis) {
        ArrayList<FPrintInfo> fPrintInfos = MiniChouContext.getmFPrintInfoList();
        long interval_min = (currentTimeMillis - lastFPrintTime) / (1000 * 60);

        Log.d(TAG,"JH-Interval:"+interval_min);
        if(fPrintInfos.get(fPrintInfos.size()-1).getmPlaceInfo().getMacList().get(0).equals(Constraints.OUTPLACE_MAC) == true)
            return false;

        if(interval_min > Constraints.STAYING_INTERVAL_MIN)
            return true;
        else
            return false;
    }

    private int getCurrentState() {
        PlaceInfo placeMatched = null;
        int state = Constraints.MNC_STATE_UNKOWN;
        for(int i=0;i<Constraints.RETRY_COUNT;i++){ //3 cycle
            if((placeMatched = getMatchedAPList()) != null) {
                Log.d(TAG,"JH-Matched PlaceInfo:"+placeMatched);
                break;
            }
            //sleep 2 sec
            SystemClock.sleep(2000);
        }

        int current = MiniChouContext.getmCurrentState();
        MiniChouContext.setmLastState(current);
        if(placeMatched != null){
            //InPlace
            state = Constraints.MNC_STATE_IN_PLACE;
            MiniChouContext.setmCurrentPlaceInfo(placeMatched);
        } else{
            //OutOfPlace
            state = Constraints.MNC_STATE_OUT_OF_PLACE;
            MiniChouContext.setmCurrentPlaceInfo(MiniChouUtils.getFakeOutPlace());
        }

        MiniChouContext.setmCurrentState(state);
        return state;


    }

    private PlaceInfo getMatchedAPList() {
        wifimanager.startScan(); //API 29 (Pie version is always returning False)
        List<ScanResult> scanResults = wifimanager.getScanResults();
        PlaceInfo rtnInfo = new PlaceInfo();


        Log.d(TAG,"JH-scanResults.size():"+scanResults.size());
            if (scanResults.size() > 0) {
                PlaceInfo searchedListN
                        = MiniChouUtils.getSortedAPListN(scanResults, Constraints.NUM_OF_SEARCHED_LIST);
                MiniChouContext.setmRealTimeSearchInfo(searchedListN); //save RealTimeSearch for Remote Adding

                ArrayList<PlaceInfo> placeInfos = MiniChouContext.getmPlaceInfoList();
                String searchedMac = "";
                ArrayList<String> regMacList;

                for(int i=0;i<searchedListN.getMacList().size();i++){
                    //Log.d(TAG,"JH-searchedListN.getMacList()-"+searchedListN.getMacList().get(i));
                    //Log.d(TAG,"JH-placeInfos.size():"+placeInfos.size());
                    for(int j=0;j<placeInfos.size();j++){
                        searchedMac = searchedListN.getMacList().get(i);

                        if( placeInfos.get(j).getMacList() == null)
                            continue;

                        regMacList = placeInfos.get(j).getMacList();
                        if(regMacList == null)
                            continue;

                        //Log.d(TAG,"JH-searched:"+searchedMac+" | regMac:"+regMacList);

                       int idx = getExtractedMatchedIdx(searchedMac,regMacList);
                       if(idx >= 0){
                           rtnInfo.setKey(placeInfos.get(j).getKey());
                           rtnInfo.getMacList().add(placeInfos.get(j).getMacList().get(idx));
                           rtnInfo.getApList().add(placeInfos.get(j).getApList().get(idx));

                           return rtnInfo;
                       }
                    }
                }
            } else {
                Log.d(TAG, "JH-startScan-Failed");
            }
        return null; //nothing matched
    }

    private int getExtractedMatchedIdx(String searchedMac, ArrayList<String> regMaclist){
        PlaceInfo rtnInfo = new PlaceInfo();


        for(int i=0 ; i<regMaclist.size(); i++){
            if(searchedMac.equals(regMaclist.get(i))){
                if(regMaclist.get(i) == null)
                    continue;

                rtnInfo.getMacList().add(regMaclist.get(i));
                return i;
            }
        }
        return -1;
    }

    private void preExecution(){
        if (null == MiniChouService.serviceIntent) {
            foregroundServiceIntent = new Intent(mContext, MiniChouService.class);
            mContext.startService(foregroundServiceIntent);
            Log.d(TAG,"JH-Service died, lauching service again");

        } else {
            foregroundServiceIntent = MiniChouService.serviceIntent;
            if(MiniChouContext.getmPlaceInfoList().size() == 0){
                //Sometime disconnected with DB server but service is still alive
                //At that time, we reset service for reconnect db
                mContext.stopService(foregroundServiceIntent); //stop Service, but alarmservice will restart service again.
                SystemClock.sleep(1000);
                Log.d(TAG,"JH-Error appeared, so service stop to restart");
            }
            //Log.d(TAG,"JH-Service still alive");
        }
    }

    private void postExecution(){
        //WorkManager Loop
        final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(WifiWorker.class)
                .setInitialDelay(30, TimeUnit.SECONDS)
                .build();

        mWorkManager.enqueue(mRequest);
    }



}