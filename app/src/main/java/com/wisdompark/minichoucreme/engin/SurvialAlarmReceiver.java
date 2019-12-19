package com.wisdompark.minichoucreme.engin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class SurvialAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, MiniChouService.class);
            context.startForegroundService(in);
        }else {
            Intent in = new Intent(context, MiniChouService.class);
            context.startService(in);
        }
    }
}
