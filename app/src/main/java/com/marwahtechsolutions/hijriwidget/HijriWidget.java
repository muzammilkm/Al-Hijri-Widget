package com.marwahtechsolutions.hijriwidget;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import com.marwahtechsolutions.hijriwidget.models.HijriAdjustor;
import com.marwahtechsolutions.hijriwidget.models.HijriCalendar;

public class HijriWidget extends AppWidgetProvider {

    private static final String TAG = "HijriWidget";

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(intent.getAction())) {
            ComponentName thisAppWidget = new ComponentName(
                    context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);

            int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                boolean isAlarm = intent.getExtras().getBoolean("isAlarm", false);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                        R.layout.main);
                updateAppWidget(context, appWidgetManager, appWidgetID, remoteViews, isAlarm);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.main);

        Log.d(TAG, String.format("Raising for Magrib is set to: %d", System.currentTimeMillis()));

        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, HijriConfig.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);

            remoteViews.setOnClickPendingIntent(R.id.llMainBox, pendingIntent);
            updateAppWidget(context, appWidgetManager, appWidgetId, remoteViews, true);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, HijriWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(android.content.Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }

    public void SetWakeUp(Context context, int hour, int minute, int appWidgetId) {
        Intent intent = new Intent(context, HijriWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context
                .getSystemService(android.content.Context.ALARM_SERVICE);
        long interval = AlarmManager.INTERVAL_DAY;
        interval = 2 * 60 * 1000;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                HijriCalendar.getNextDayMilliSeconds(hour, minute), interval, pendingIntent);

        Log.d(TAG, String.format("Magrib is set to: %d: %d %d", hour, minute, System.currentTimeMillis()));

    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, RemoteViews remoteViews,
                                       boolean setAlarm) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        HijriAdjustor hijriAdjustor = new HijriAdjustor(prefs.getString("pMoonSight", HijriAdjustor.getDefaultValue()));
        int noOfDays = hijriAdjustor.getDays();
        int locale = HijriCalendar.getLocale(prefs.getString("pLanguage", "English"));
        int background = prefs.getInt("pTheme", Color.BLACK);

        if (setAlarm) {
             SetWakeUp(context, hijriAdjustor.getHour(), hijriAdjustor.getMinute(), appWidgetId);
        }

        Map<Integer, String[]> arrHijriMonths = new HashMap<Integer, String[]>();
        Map<Integer, String[]> arrHijriDayOfWeek = new HashMap<Integer, String[]>();

        arrHijriMonths.put(HijriCalendar.AR, context.getResources().getStringArray(R.array.hijri_months_arabic));
        arrHijriMonths.put(HijriCalendar.EN, context.getResources().getStringArray(R.array.hijri_months_english));

        arrHijriDayOfWeek.put(HijriCalendar.AR, context.getResources().getStringArray(R.array.hijri_days_arabic));
        arrHijriDayOfWeek.put(HijriCalendar.EN, context.getResources().getStringArray(R.array.hijri_days_english));



        HijriCalendar dateHijri = new HijriCalendar(noOfDays,
                locale,
                arrHijriMonths.get(locale),
                arrHijriDayOfWeek.get(locale),
                hijriAdjustor.getHour(),
                hijriAdjustor.getMinute());

        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);

        remoteViews.setInt(R.id.llDateBox, "setBackgroundColor", Color.rgb(r,g,b));
        remoteViews.setInt(R.id.llDateMonthYearBox, "setBackgroundColor", background);


        remoteViews.setTextViewText(R.id.tvDay, dateHijri.getDay());
        remoteViews.setTextViewText(R.id.tvSuffix, dateHijri.getSuffix());
        remoteViews.setTextViewText(R.id.tvMonth, dateHijri.GetMonthName());
        remoteViews.setTextViewText(R.id.tvYear, dateHijri.getFormatedYear());
        remoteViews.setTextViewText(R.id.tvDayName, dateHijri.GetDayName());
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}
