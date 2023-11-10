package com.marwahtechsolutions.hijriwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.marwahtechsolutions.hijriwidget.models.HijriWidgetCalendar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HijriWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "HijriWidget";
    private static final String WORK_NAME = "HijriWidgetWorker";
    private static final int UPDATE_INTERVAL = 30;

    private final int layoutId;

    public HijriWidgetProvider(int layoutId) {
        this.layoutId = layoutId;
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        int locale = getLocale(context, prefs);
        int adjustedNumberOfDays = getAdjustNoOfDays(context, prefs);
        String maghribTime = getMagribTime(context, prefs);

        Map<Integer, String[]> arrHijriMonths = getHijriNames(context, R.array.hijri_months_arabic, R.array.hijri_months_english);
        Map<Integer, String[]> arrHijriDayOfWeek = getHijriNames(context, R.array.hijri_days_arabic, R.array.hijri_days_english);

        HijriWidgetCalendar dateHijri = new HijriWidgetCalendar(locale,
                arrHijriMonths,
                arrHijriDayOfWeek,
                adjustedNumberOfDays,
                maghribTime
        );

        Log.d(TAG, String.format("Current Hijri Date %s %d", dateHijri, this.layoutId));

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), this.layoutId);

        // Refreshing the view

        remoteViews.setTextViewText(R.id.tvDay, dateHijri.getDay());
        remoteViews.setTextViewText(R.id.tvSuffix, dateHijri.getDaySuffix());
        remoteViews.setTextViewText(R.id.tvMonth, dateHijri.getMonthName());
        remoteViews.setTextViewText(R.id.tvYear, dateHijri.getFormattedYear());
        remoteViews.setTextViewText(R.id.tvDayName, dateHijri.getDayName());


        // Setting Click Intent
        Intent intent = new Intent(context, HijriWidgetSettings.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        remoteViews.setOnClickPendingIntent(R.id.llMainBox, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        PeriodicWorkRequest saveRequest = new PeriodicWorkRequest
                .Builder(HijriWidgetWorker.class, UPDATE_INTERVAL, TimeUnit.MINUTES)
                .build();

        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                saveRequest);
    }

    @Override
    public void onDisabled(Context context) {
        WorkManager
                .getInstance(context)
                .cancelUniqueWork(WORK_NAME);
    }

    private static Map<Integer, String[]> getHijriNames(Context context, int hijri_arabic, int hijri_english) {
        Map<Integer, String[]> arrHijriNames = new HashMap<>();
        arrHijriNames.put(HijriWidgetCalendar.AR, context.getResources().getStringArray(hijri_arabic));
        arrHijriNames.put(HijriWidgetCalendar.EN, context.getResources().getStringArray(hijri_english));
        return arrHijriNames;
    }

    private static int getLocale(Context context, SharedPreferences prefs) {
        String pLanguage = getPrefString(context, prefs, "pLanguage", R.string.default_language);
        if (pLanguage.equalsIgnoreCase("AR"))
            return HijriWidgetCalendar.AR;
        return HijriWidgetCalendar.EN;
    }

    private static int getAdjustNoOfDays(Context context, SharedPreferences prefs) {
        String pAdjustedNoOfDays = getPrefString(context, prefs, "pAdjustedNoOfDays", R.string.default_adjust_no_of_days);
        return Integer.parseInt(pAdjustedNoOfDays);
    }

    private static String getMagribTime(Context context, SharedPreferences prefs) {
        return getPrefString(context, prefs, "pMagribTime", R.string.default_maghrib_time);
    }

    @NonNull
    private static String getPrefString(Context context, SharedPreferences prefs, String key, int defaultRes) {
        String defaultValue = context.getResources().getString(defaultRes);
        return prefs.getString(key, defaultValue);
    }
}