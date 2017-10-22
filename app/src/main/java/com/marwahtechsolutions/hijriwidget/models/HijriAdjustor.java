package com.marwahtechsolutions.hijriwidget.models;

import static java.lang.Integer.parseInt;

/**
 * Created by Muzammil Khaja Mohammed on 10/9/2017.
 */

public class HijriAdjustor {
    private int days;
    private int hour;
    private int minute;

    public HijriAdjustor(String pref) {
        String[] arr = pref.split(",");
        hour = Integer.parseInt(arr[0]);
        minute = Integer.parseInt(arr[1]);
        days = Integer.parseInt(arr[2]);
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String toString(){
     return String.format("%d,%d,%d",hour, minute, days);
    }

    public String timeFormat(){
        String meridiem = "am";
        int meridiemHour = 0;
        if(hour >12 ){
            meridiem = "pm";
            meridiemHour = 12;
        }
        return String.format("%02d:%02d %s",hour - meridiemHour, minute, meridiem);
    }

    public String daysFormat(){
        return String.format("%d", days);
    }

    public static String getDefaultValue() {
        return "6,0,0";
    }
}
