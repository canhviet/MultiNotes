package com.example.multinotes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            if ("STOP_SERVICE".equals(action)) {
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
