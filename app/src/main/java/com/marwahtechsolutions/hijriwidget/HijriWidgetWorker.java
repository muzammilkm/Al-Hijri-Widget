package com.marwahtechsolutions.hijriwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.Arrays;

public class HijriWidgetWorker extends Worker {
    private final Context context;

    public HijriWidgetWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @Override
    public Result doWork() {
        Log.d(HijriWidgetProvider.TAG, "doWork: Large");
        this.sendBroadcast(HijriWidgetProviderLarge.class);
        Log.d(HijriWidgetProvider.TAG, "doWork: Small");
        this.sendBroadcast(HijriWidgetProviderSmall.class);
        return Result.success();
    }

    private void sendBroadcast(@NonNull Class<?> cls) {
        int[] appWidgetIds = HijriWidgetProvider.getAppWidgetIds(context, cls);
        if (appWidgetIds.length > 0) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, cls);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            context.sendBroadcast(intent);
        }
        Log.d(HijriWidgetProvider.TAG, String.format("sendBroadcast: %s", Arrays.toString(appWidgetIds)));
    }
}
