package com.marwahtechsolutions.hijriwidget.models;

public class Time {
    public static int parseHour(String value) {
        try {
            String[] time = value.split(":");
            return (Integer.parseInt(time[0]));
        } catch (Exception e) {
            return 0;
        }
    }

    public static int parseMinute(String value) {
        try {
            String[] time = value.split(":");
            return (Integer.parseInt(time[1]));
        } catch (Exception e) {
            return 0;
        }
    }

    public static String toString(int h, int m) {
        return String.format("%02d:%02d", h, m);
    }

    public static String to12HourFormat(int h, int m) {
        String meridiem = h > 11 ? "PM" : "AM";
        h = (h %= 12) == 0 ? 12 : h;
        return String.format("%02d:%02d %s", h, m, meridiem);
    }

}
