package com.marwahtechsolutions.hijriwidget.models;

import android.icu.util.Calendar;
import android.icu.util.IslamicCalendar;
import androidx.annotation.NonNull;
import java.util.Map;

public class HijriWidgetCalendar {

    public final static int AR = 0;
    public final static int EN = 1;

    private final IslamicCalendar _calendar;
    private final int _locale;
    private final String[] _arrHijriDayOfWeek;
    private final String[] _arrHijriMonths;

    public HijriWidgetCalendar(int locale,
                               Map<Integer, String[]> arrHijriMonths,
                               Map<Integer, String[]> arrHijriDayOfWeek,
                               int adjustedNumberOfDays,
                               String maghribTime) {
        _calendar = new IslamicCalendar();
        _calendar.setCalculationType(IslamicCalendar.CalculationType.ISLAMIC_UMALQURA);
        int currentHour = _calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = _calendar.get(Calendar.MINUTE);
        int magribHour = Time.parseHour(maghribTime);
        int maghribMinute = Time.parseMinute(maghribTime);

        if (currentHour > magribHour ||
                (currentHour == magribHour && currentMinute >= maghribMinute)) {
            adjustedNumberOfDays += 1;
        }

        _calendar.add(Calendar.DAY_OF_MONTH, adjustedNumberOfDays);
        _locale = locale;
        _arrHijriDayOfWeek = arrHijriDayOfWeek.get(locale);
        _arrHijriMonths = arrHijriMonths.get(locale);
    }

    public String getDay() {
        return this.get(Calendar.DAY_OF_MONTH);
    }

    public String getDaySuffix() {
        if (_locale == AR)
            return "";

        int day = _calendar.get(Calendar.DAY_OF_MONTH);
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public String getDayName() {
        int day = _calendar.get(Calendar.DAY_OF_WEEK);
        return this._arrHijriDayOfWeek[day - 1];
    }

    public String getMonthName() {
        int month = _calendar.get(Calendar.MONTH);
        return this._arrHijriMonths[month];
    }

    public String getFormattedYear() {
        return String.format("%s %s", this.get(Calendar.YEAR), this.getERA());
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("%s/%s/%s",
                this.getDay(), this.getMonthName(), this.getFormattedYear());
    }

    private String getERA() {
        if (_locale == AR)
            return "";
        return "AH";
    }

    private String get(int field) {
        int result = _calendar.get(field);
        return String.format("%s", result);
    }
}
