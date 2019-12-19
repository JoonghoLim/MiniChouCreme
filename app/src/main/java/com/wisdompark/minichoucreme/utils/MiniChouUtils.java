package com.wisdompark.minichoucreme.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.wisdompark.minichoucreme.storage.PlaceInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MiniChouUtils {
    private static final String TAG = "MiniChouUtils";
    private static String mUserId = null;
    public static String getSenderID(Context context){
        //각 Device의 Android ID를 고유 식별자로 구분한다
        String idByANDROID_ID
                = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return idByANDROID_ID;
    }

    public static PlaceInfo getFakeOutPlace(){
        String fakeKey = Constraints.OUTPLACE_KEY;
        ArrayList<String> fakeMac = new ArrayList<>();
        ArrayList<String> fakeAP = new ArrayList<>();

        fakeMac.add(Constraints.OUTPLACE_MAC);
        fakeAP.add(Constraints.OUTPLACE_APNAME);

        return new PlaceInfo(
                fakeKey
                ,fakeMac
                ,fakeAP);
    }

    public static String getUserId(Context context) {
        if(mUserId == null){
            mUserId = getSenderID(context);
        }
        return mUserId;
    }

    public static void setUserId(String uId) {
        mUserId = uId;
    }

    static private Comparator<ScanResult> comparator = new Comparator<ScanResult>() {
        @Override
        public int compare(ScanResult lhs, ScanResult rhs) {
            return (lhs.level > rhs.level ? -1 : (lhs.level==rhs.level ? 0 : 1));
        }
    };

    public static PlaceInfo getSortedAPListN(List<ScanResult> results, int N){

        ArrayList<String> apList = new ArrayList<>();
        ArrayList<String> macList = new ArrayList<>();
        Collections.sort(results, comparator);

        int count = 0;
        /*
        if(results.size() >= N)
            count = N;
        else
            count = results.size();

        for(ScanResult result : results){
            Log.d(TAG,"JH-"+result);
        }
        */
        for(int i=0;i<results.size() && count < N;i++){
            if(results.get(i).SSID.length() == 0)
                continue;
            apList.add(results.get(i).SSID);
            macList.add(results.get(i).BSSID);
            count++;
        }

        return new PlaceInfo(""+System.currentTimeMillis(),macList,apList); //현재 시간을 Key로 사용하자
    }

    public static String mills2Date(long mills, int type){
        SimpleDateFormat format=new SimpleDateFormat();
        switch (type){
            case 0:
                format = new SimpleDateFormat("MM월 dd일 HH시mm분ss초");
                break;
            case 1:
                format = new SimpleDateFormat("yyyy-MM-dd");
                break;
            case 2:
                format = new SimpleDateFormat("yy/MM/dd");
                break;
            case 3:
                format = new SimpleDateFormat("HH:mm:ss");
                break;
            case 4:
                format = new SimpleDateFormat("hh:mm:ss a");
                break;
            case 5:
                format = new SimpleDateFormat("오늘은 yyyy년의 w주차이며 D번째 날입니다.");
                break;
            case 6:
                format = new SimpleDateFormat("오늘은 M월의 w번째 주, d번째 날이며, F번째 E요일입니다.");
                break;
            default:
                format = new SimpleDateFormat("yyyy년 MM월 dd일 E요일");
                break;
        }

        return format.format(mills);

    }

    public static void setPreference(Context context, String key, String value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.apply();
    }

    public static String getPreference(Context context, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        String value = prefs.getString(key, null);
        return value;
    }
}
