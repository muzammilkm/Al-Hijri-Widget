package com.marwahtechsolutions.hijriwidget;

import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.marwahtechsolutions.hijriwidget.models.HijriAdjustor;
import com.marwahtechsolutions.hijriwidget.models.HijriCalendar;

public class HijriWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "Al Hijri Widget";
    public static final String ACTION_UPDATE = "com.marwahtechsolutions.hijriwidget.action.UPDATE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, String.format("On Received %s %d", intent.getAction(), System.currentTimeMillis()));
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
        for (int appWidgetID : ids) {
            switch (intent.getAction()){
                case AppWidgetManager.ACTION_APPWIDGET_CONFIGURE:
                case ACTION_UPDATE:
                case Intent.ACTION_SCREEN_ON:
                    updateAppWidget(context, appWidgetManager, appWidgetID, remoteViews);
                break;
            }
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        Log.d(TAG, String.format("On Update %d ", System.currentTimeMillis()));
        for (int appWidgetID : appWidgetIds) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_SCREEN_ON);

            Context applicationContext = context.getApplicationContext();
            if (applicationContext != null) {
                applicationContext.registerReceiver(this, intentFilter);
            } else {
                Log.d("applicationContext", "is null and should not be null");
            }

            Intent intent = new Intent(context, HijriConfig.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.main);
            remoteViews.setOnClickPendingIntent(R.id.llMainBox, pendingIntent);
            updateAppWidget(context, appWidgetManager, appWidgetID, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (int appWidgetID : appWidgetIds) {
            Intent updateIntent = new Intent(context, HijriWidgetProvider.class);
            updateIntent.setAction(ACTION_UPDATE);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    updateIntent, PendingIntent.FLAG_NO_CREATE);
            alarmManager.cancel(pendingIntent);
        }
        super.onDeleted(context, appWidgetIds);
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, RemoteViews remoteViews) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        HijriAdjustor hijriAdjustor = new HijriAdjustor(prefs.getString("pMoonSight", context.getResources().getString(R.string.app_setting_default_moonSighting)));
        int noOfDays = hijriAdjustor.getDays();
        int locale = HijriCalendar.getLocale(prefs.getString("pLanguage", context.getResources().getString(R.string.app_setting_default_language)));
        int background = prefs.getInt("pTheme", context.getResources().getColor(R.color.defaultThemeColor));

        Log.d(TAG, "Repainting View");

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
