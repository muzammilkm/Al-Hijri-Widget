package com.marwahtechsolutions.hijriwidget;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.marwahtechsolutions.hijriwidget.models.Logger;

/**
 * Created by MKhaja on 3/25/2018.
 */

public class WidgetService extends Service {
    private static final String TAG = "Al Hijri Widget";

    @Override
    public void onCreate() {
        Logger.d(TAG, "Registering Intent Action Screen On");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(HijriWidgetProvider.receiver, filter);
    }

    @Override
    public void onDestroy(){
        Logger.d(TAG, "unregistering");
        unregisterReceiver(HijriWidgetProvider.receiver);
    }

    @Override
    public IBinder onBind(Intent in) {
        return null;
    }
}
