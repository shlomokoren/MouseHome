package com.momo.mousehome;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MouseHomeService extends Service {
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(() -> {
            try { RootRunner.startDaemon(); } catch (Exception ignored) {}
            stopSelf();
        }).start();
        return START_NOT_STICKY;
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
