package com.marwahtechsolutions.hijriwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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

        ComponentName componentName = new ComponentName(context, HijriWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(componentName);

        for (int appWidgetId : appWidgetIds) {
            Log.d(HijriWidget.TAG, String.format("On doWork - widgets: %d", appWidgetId));
        }
        Intent intent = new Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context,
                HijriWidget.class
        );
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(intent);

        return Result.success();
    }
}
