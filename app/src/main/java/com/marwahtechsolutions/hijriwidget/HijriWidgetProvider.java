package com.marwahtechsolutions.hijriwidget;

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
import com.marwahtechsolutions.hijriwidget.models.Logger;

public class HijriWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "Al Hijri Widget";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Logger.d(TAG, String.format("On Received %s %d", intent.getAction(), System.currentTimeMillis()));

        ComponentName thisAppWidget = new ComponentName(
                context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.main);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        boolean isAlarm = false;
        if (intent.getExtras() != null) {
            isAlarm = intent.getExtras().getBoolean("isAlarm", false);
        }
        if (AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(intent.getAction()) ||
                AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            for (int appWidgetID : ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID, remoteViews, isAlarm);
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Logger.d(TAG, String.format("On Update %d ", System.currentTimeMillis()));
        for (int appWidgetID : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.main);
            remoteViews.setOnClickPendingIntent(R.id.llMainBox, CreateClickIntent(context, appWidgetID));
            updateAppWidget(context, appWidgetManager, appWidgetID, remoteViews, true);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        for (int appWidgetID : appWidgetIds) {
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(android.content.Context.ALARM_SERVICE);
            alarmManager.cancel(CreateConfigureIntent(context, appWidgetID));
        }
    }

    private PendingIntent CreateClickIntent(Context context, int appWidgetID) {
        Intent intent = new Intent(context, HijriConfig.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent CreateConfigureIntent(Context context, int appWidgetID) {
        Intent intent = new Intent(context, HijriWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void SetWakeUp(Context context, int hour, int minute, int appWidgetId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = CreateConfigureIntent(context, appWidgetId);
        //alarmManager.cancel(pendingIntent);
        long interval = AlarmManager.INTERVAL_HOUR;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                HijriCalendar.getNextDayMilliSeconds(hour, minute), interval, pendingIntent);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, RemoteViews remoteViews,
                                 boolean setAlarm) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        HijriAdjustor hijriAdjustor = new HijriAdjustor(prefs.getString("pMoonSight", context.getResources().getString(R.string.app_setting_default_moonSighting)));
        int noOfDays = hijriAdjustor.getDays();
        int locale = HijriCalendar.getLocale(prefs.getString("pLanguage", context.getResources().getString(R.string.app_setting_default_language)));
        int background = prefs.getInt("pTheme", context.getResources().getColor(R.color.defaultThemeColor));

        if (setAlarm) {
            SetWakeUp(context, hijriAdjustor.getHour(), hijriAdjustor.getMinute(), appWidgetId);
            Logger.d(TAG, String.format("Magrib is set to: %s", hijriAdjustor.timeFormat()));
        }
        Logger.d(TAG, "Repainting View");

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

        remoteViews.setInt(R.id.llDateBox, "setBackgroundColor", background);
        remoteViews.setInt(R.id.llDateMonthYearBox, "setBackgroundColor", background);


        remoteViews.setTextViewText(R.id.tvDay, dateHijri.getDay());
        remoteViews.setTextViewText(R.id.tvSuffix, dateHijri.getSuffix());
        remoteViews.setTextViewText(R.id.tvMonth, dateHijri.GetMonthName());
        remoteViews.setTextViewText(R.id.tvYear, dateHijri.getFormatedYear());
        remoteViews.setTextViewText(R.id.tvDayName, dateHijri.GetDayName());
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}
