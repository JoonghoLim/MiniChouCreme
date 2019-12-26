package com.wisdompark.minichoucreme.engin;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.ui.MainActivity;
import com.wisdompark.minichoucreme.utils.Constraints;
import com.wisdompark.minichoucreme.utils.MiniChouUtils;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MiniChouService extends Service {
    private static final String TAG = "MiniChouService";
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public static Intent serviceIntent = null;

    private static int MESSAGE_WHAT = 0;
    private static Handler handler= new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.what == MESSAGE_WHAT){
                Log.d(TAG,"JH-Servie check service alive or not");
                handler.sendEmptyMessageDelayed(MESSAGE_WHAT,15000);
            }
            return false;
        }
    });

    public MiniChouService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final WorkManager mWorkManager = WorkManager.getInstance();
        final OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(WifiWorker.class)
                .setInitialDelay(3, TimeUnit.SECONDS) //waiting for DB loading
                .build();

        MiniChouContext.setmUserID(MiniChouUtils.getSenderID(this));
        MiniChouContext.getmPlaceInfoList().clear(); //lIst 초기화
        MiniChouContext.getmFPrintInfoList().clear();//lIst 초기화

        settingDB();

        mWorkManager.cancelAllWork();
        mWorkManager.enqueue(mRequest);

        setPreferences();

        handler.sendEmptyMessageDelayed(MESSAGE_WHAT,15000);


        Log.d(TAG,"JH-MyEmail:"+MiniChouContext.getMyEmail());
        Log.d(TAG,"JH-isIsParentsMode:"+MiniChouContext.isIsParentsMode());
        Log.d(TAG,"JH-Watching Email:"+MiniChouContext.getWatching_email());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForgroundServiceWithNotification();
        //}

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        Intent intent = new Intent(this, SurvialAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        Log.d(TAG,"JH-onDestroy()");
        clearDB();

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        Intent intent = new Intent(this, SurvialAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        clearDB();
        Log.d(TAG,"JH-onTaskRemoved()");
    }

    private void startForgroundServiceWithNotification()
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        //builder.setSmallIcon(R.drawable.ic_menu_slideshow); //R.mipmap.ic_choux_launcher
        //builder.setSmallIcon(R.mipmap.ic_choux_launcher);
        builder.setSmallIcon(R.drawable.ic_choux_noti);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.setBigContentTitle("미니슈 서비스 실행중");
        style.bigText("기타 정보를 보려면 누르세요");
        builder.setContentTitle("미니슈 서비스 실행중");
        builder.setContentText("기타 정보를 보려면 누르세요");
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(true);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("1", "undead_service", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    private void setPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchSP = sp.getBoolean("sync",false);
        String strEmail = sp.getString("watching_email","");

        MiniChouContext.setIsParentsMode(switchSP);
        MiniChouContext.setWatching_email(strEmail);
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
                .child(Constraints.COMMAND_SEND_NAME)
                .addValueEventListener( CommandValueEventListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_RESPONSE_NAME)
                .addValueEventListener( CommandRESPValueEventListener.getInstance(this));
    }
/*
    private void resettingDB() {
            //clearDB();
            settingDB();
    }
 */

    private void clearDB() {
        String emailAddress = MiniChouContext.getWatching_email();

        databaseReference.child(emailAddress)
                .child(Constraints.FPRINT_NAME).removeEventListener(FPrintChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.PLACE_NAME)
                .removeEventListener( PlaceChildListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_SEND_NAME)
                .removeEventListener( CommandValueEventListener.getInstance(this));

        databaseReference.child(emailAddress)
                .child(Constraints.COMMAND_RESPONSE_NAME)
                .removeEventListener( CommandRESPValueEventListener.getInstance(this));
    }
}
