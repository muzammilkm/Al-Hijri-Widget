package com.marwahtechsolutions.hijriwidget;

import java.util.HashMap;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.marwahtechsolutions.hijriwidget.models.HijriAdjustor;
import com.marwahtechsolutions.hijriwidget.models.HijriCalendar;
import com.marwahtechsolutions.hijriwidget.models.Logger;

public class HijriWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "Al Hijri Widget";
    public static final HijriWidgetProvider receiver = new HijriWidgetProvider();

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, String.format("On Received %s ", intent.getAction()));
        super.onReceive(context, intent);

        ComponentName thisAppWidget = new ComponentName(
                context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);
        for (int appWidgetID : ids) {
            switch (intent.getAction()){
                case AppWidgetManager.ACTION_APPWIDGET_CONFIGURE:
                    Logger.d(TAG, String.format("On Configuration Set On %s", intent.getAction()));
                    updateAppWidget(context, appWidgetManager, appWidgetID);
                    break;
                case Intent.ACTION_SCREEN_ON:
                    Logger.d(TAG, String.format("On Screen On %s", intent.getAction()));
                    updateAppWidget(context, appWidgetManager, appWidgetID);
                break;
            }
        }
    }

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Logger.d(TAG, "On Update");
        context.startService(new Intent(context, WidgetService.class));
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDisabled(Context context){
        Logger.d(TAG, "On Disabled");
        context.stopService(new Intent(context, WidgetService.class));
    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.main);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        HijriAdjustor hijriAdjustor = new HijriAdjustor(prefs.getString("pMoonSight", context.getResources().getString(R.string.app_setting_default_moonSighting)));
        int noOfDays = hijriAdjustor.getDays();
        int locale = HijriCalendar.getLocale(prefs.getString("pLanguage", context.getResources().getString(R.string.app_setting_default_language)));
        int background = prefs.getInt("pTheme", context.getResources().getColor(R.color.defaultThemeColor));

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

        // Refreshing the view
        remoteViews.setInt(R.id.llDateBox, "setBackgroundColor", background);
        remoteViews.setInt(R.id.llDateMonthYearBox, "setBackgroundColor", background);

        remoteViews.setTextViewText(R.id.tvDay, dateHijri.getDay());
        remoteViews.setTextViewText(R.id.tvSuffix, dateHijri.getSuffix());
        remoteViews.setTextViewText(R.id.tvMonth, dateHijri.getMonthName());
        remoteViews.setTextViewText(R.id.tvYear, dateHijri.getFormatedYear());
        remoteViews.setTextViewText(R.id.tvDayName, dateHijri.getDayName());

        Logger.d(TAG, String.format("Current Hijri Date %s", dateHijri.toString()));

        // Setting Click Intent
        Intent intent = new Intent(context, HijriConfig.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.llMainBox, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}
